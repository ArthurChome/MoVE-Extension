/**
  * Copied from
  * https://gist.github.com/timmolderez/5af5adfa60e62989846750ccc01cb7a7
  */

package de.thm.move

import de.thm.move.controllers.MoveCtrl
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.{Parent, Scene}
import javafx.stage.{Stage, WindowEvent}
import org.testfx.framework.junit.ApplicationTest


/**
  * Base class for writing TestFX tests: gets used by all other test classes.
  * The code originates from: https://gist.github.com/timmolderez/5af5adfa60e62989846750ccc01cb7a7
  */

abstract class GuiTest extends ApplicationTest {
  protected var ctrl: MoveCtrl = _

  protected var scene: Scene = _

  override def start(stage: Stage): Unit = {
    val windowWidth = Global.config.getDouble("window.width").getOrElse(600.0)
    val windowHeight = Global.config.getDouble("window.height").getOrElse(600.0)

    val fxmlLoader = new FXMLLoader(MoveApp.getClass.getResource("/fxml/move.fxml"))
    fxmlLoader.setResources(Global.fontBundle)
    val mainViewRoot: Parent = fxmlLoader.load()
    scene = new Scene(mainViewRoot)
    scene.getStylesheets.add(Global.styleSheetUrl)

    stage.setTitle(Global.config.getString("window.title").getOrElse(""))
    stage.setScene(scene)
    stage.setWidth(windowWidth)
    stage.setHeight(windowHeight)
    stage.show()

    ctrl = fxmlLoader.getController[MoveCtrl]
    ctrl.setupMove(stage, None)

    stage.setOnCloseRequest(new EventHandler[WindowEvent] {
      override def handle(event: WindowEvent): Unit = {
        ctrl.shutdownMove()
      }
    })
  }
}
