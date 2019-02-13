package de.thm.move

import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.{Path,Paths}
import javax.imageio.ImageIO
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import de.thm.move.controllers.MoveCtrl
import de.thm.move.models.ModelicaCodeGenerator

/**
  * This Runnable will be run in the JavaFX thread to produce a
  * snapshot of the application
  */
class snapshotRunnable(scene: Scene) extends Runnable {
  var returnedSnapshot: BufferedImage = _
  
  override def run = {
    val snapshot = scene.snapshot(null)
    returnedSnapshot = SwingFXUtils.fromFXImage(snapshot, null)
  }
 }

/**
  * Object to contain the code to help tests save to a file
  */
object SaveUtil {
  val comparisonResourceDirectory = "comparison"

  def saveToFile(ctrl: MoveCtrl, fileName: String, pixelsPerMM: Int): Unit = {
    val fxmlLoader = new FXMLLoader(MoveApp.getClass.getResource("/fxml/move.fxml"))
    fxmlLoader.setResources(Global.fontBundle)
    val windowWidth = Global.config.getDouble("window.width").getOrElse(600.0)
    val windowHeight = Global.config.getDouble("window.height").getOrElse(600.0)
    ctrl.getFileCtrl.generateCodeAndWriteToFile(
      ctrl.getDrawPanel.getShapes,
      windowWidth,
      windowHeight) (
      Right(Paths.get(fileName)),
      pixelsPerMM,
      ModelicaCodeGenerator.FormatSrc.Pretty)
  }

  /**
    * Produces a snapshot of the given scene
    */
  def makeSnapshot(scene: Scene): BufferedImage = {
    import javafx.application.Platform
    
    // We need to use a separate Runnable to schedule this to run in
    // the JavaFX thread, otherwise it won't work.  
    val runnable = new snapshotRunnable(scene)
    
    // Schedule it for running on the JavaFX thread
    Platform.runLater(runnable)
    
    // Wait for it to complete, so we know that when this method
    // returns, the screenshot has been taken.  Apparently thread.join
    // does not work, so we resort to this inelegant (but working)
    // solution
    while (runnable.returnedSnapshot == null) {
      Thread.sleep(100)
    }
    runnable.returnedSnapshot
  }

  /**
    * Takes a scene (like the one from GuiTest.scene) and a filename
    * (taken from the present working directory) and saves a screenshot
    * of the MoVe application to that filename
    */
  def saveSnapshot(scene: Scene, filenameFromPWD: String): Unit = {
    val snapshotAsBufferedImage = makeSnapshot(scene)
    ImageIO.write(snapshotAsBufferedImage, "png", new File(filenameFromPWD))
  }

  /**
    * Reads a snapshot from a file (filename taken from the present
    * working directory)
    */
  def loadSnapshot(filenameFromPWD: String): BufferedImage = {
    ImageIO.read(new File(filenameFromPWD))
  }

  /**
    * This method is used to compare two BufferedImages for equality on
    * the data level
    */
  def bufferedImagesEqual(bufImgA: BufferedImage, bufImgB: BufferedImage): Boolean = {
    if (bufImgA.getWidth != bufImgB.getWidth ||
        bufImgA.getHeight != bufImgB.getHeight) {
      return false
    }
    var x: Integer = 0
    var y: Integer = 0
    for (x <- 0 until bufImgA.getWidth;
         y <- 0 until bufImgA.getHeight) {
      if (bufImgA.getRGB(x,y) != bufImgB.getRGB(x,y)) {
        return false
      }
    }
    return true
  }

  /**
    * To test whether a snapshot of the current scene is equal to a saved
    * file from a known directory in the resources directory 
    */
  def currentSnapshotEqualsFile(scene: Scene, resourceFilename: String): Boolean = {
    val currentSnapshot = makeSnapshot(scene)
    try {
      val loadedSnapshot = loadSnapshot(
        getClass.getResource("/" + comparisonResourceDirectory + "/" +
                             resourceFilename).getFile)
      bufferedImagesEqual(currentSnapshot, loadedSnapshot)
    } catch {
      case e: Exception => {
        e.printStackTrace
        false
      }
    }
  }
}
