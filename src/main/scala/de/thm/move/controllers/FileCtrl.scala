/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.controllers

import java.net.URI
import java.nio.file.{Files, Path, Paths}

import de.thm.move.Global._
import de.thm.move.models.ModelicaCodeGenerator.FormatSrc._
import de.thm.move.models.{ModelicaCodeGenerator, SrcFile, SvgCodeGenerator, UserInputException}
import de.thm.move.views.dialogs.Dialogs
import de.thm.move.models.house.House
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Node
import javafx.stage.Window
import javax.imageio.ImageIO
import play.api.libs.json.Json

import scala.util.{Failure, Success, Try}

/** Controller for interaction with files. (opening, saving, exporting)
  * This controller asks the user to select a appropriate file and uses the selected file for its
  * functions.
  */
class FileCtrl(owner: Window) {

  /** Contains the path of the current open file */
  private var openedFile: Option[Path] = None

  /**
    * Reads and create a house object by parsing a json string located in the given path.
    * @param path: The path of the file containing the house to be created.
    * @return A House object
    */
  private def parseHouse(path: Path): House = {
    val stream = Files.newInputStream(path)
    val json_input = try {Json.parse(stream)} finally {stream.close()}
    val house: House = Json.fromJson[House](json_input).get
    house
  }

  /**
    * Let the user choose a json file; parses this file and returns the
    * path to the file and the house constructed by parsing the json content.
    * @return A path to the file and a House object
    */
  def openFile: Try[(Path, House)] = {
    val chooser = Dialogs.newJsonFileChooser()
    chooser.setTitle("Open..")

    val fileTry = Option(chooser.showOpenDialog(owner)) match {
      case Some(x) =>
        if (Paths.get(x.toURI).getFileName.toString.endsWith(".json")) Success(x)
        else Failure(UserInputException("Select a JSON file for saving!"))
      case _ => Failure(UserInputException("Select a file to open!"))
    }
    for {
      file <- fileTry
      path = Paths.get(file.toURI)
      house <- openFile(path)
    } yield (path, house)
  }

  /**
    * Tries to open an existing file given by a path and parse its contents into a House object.
    * @param path Path of the file to parse
    * @return  Returns in case of success a House object, or either a fail.
    */
  def openFile (path: Path): Try[House] = {
    val existsTry =
      if (Files.exists(path)) Success(path)
      else Failure(UserInputException(s"$path doesn't exist!"))
    for {
      _ <- existsTry
    } yield {
      val house = parseHouse(path)
      openedFile = Some(path)
      house
    }
  }

  /**
    * Writes a given input string into a target file given by its URI.
    * @param input String to be written into the target.
    * @param target URI of the target file
    */
  def writeToFile(input:String)(target:URI): Unit = {
    val path = Paths.get(target)
    val writer = Files.newBufferedWriter(path, encoding)
    try {writer.write(input)} finally {writer.close()}
  }

  /**
    * Converts a given House to json and writes it to an existing file.
    * If there is no existing file the user get asked to save a new file.
    */
  def saveFile(house: House): Try[Path] = {
    val codeGen = Json.toJson(house).toString()
    if (openedFile.isEmpty){
      saveAsFile(house)
    }
    else {
      val filepath = openedFile.get
      val targetUri = filepath.toUri
      writeToFile(codeGen)(targetUri)
      openedFile = Some(filepath)
      Success(filepath)
    }
  }

  /**
    * Converts a house into a json string, lets the user choose a target file and writes the string to that file.
    * @param house House object to be converted to json and saved
    * @return A success or failure (Try), if its successful it contains the path of the target file
    */
  def saveAsFile(house: House): Try[Path] = {
    val codeGen = Json.toJson(house).toString()
    val chooser = Dialogs.newJsonFileChooser()

    chooser.setTitle("Save as..")
    val fileTry = Option(chooser.showSaveDialog(owner)) match {
      case Some(x) => Success(x)
      case _ => Failure(UserInputException("Select a file for saving!"))
    }
    for {
      file <- fileTry
      filepath = Paths.get(file.toURI)
    } yield {
      val targetUri = filepath.toUri
      writeToFile(codeGen)(targetUri)
      openedFile = Some(filepath)
      filepath
    }
  }

  /**
    * Generates modelica code and writes it to a file.
    * (Useful for testing snapshot compares)
    **/
  def generateCodeAndWriteToFile(shapes:List[Node],
                                         width:Double,
                                         height:Double)
                                        (srcEither:Either[SrcFile, Path],
                                         pxPerMm:Int,
                                         format:FormatSrc): Unit = {
    val generator = new ModelicaCodeGenerator(format, pxPerMm, width, height)
    srcEither match {
      case Left(src) =>
        val targetUri = src.file.toUri
        val lines = generator.generateExistingFile(src.model.name, targetUri, shapes)
        val before = src.getBeforeModel
        val after = src.getAfterModel
        generator.writeToFile(before,lines, after)(targetUri)
      case Right(filepath) =>
        val targetUri = filepath.toUri
        val filenamestr = Paths.get(targetUri).getFileName.toString
        val modelName = if(filenamestr.endsWith(".mo")) filenamestr.dropRight(3) else filenamestr
        val lines = generator.generate(modelName, targetUri, shapes)
        generator.writeToFile("",lines, "")(targetUri)
    }
  }

  /**
    * Exports the given Icon represented by
    * the given shapes and width,height into an user-selected svg-file
    */
  def exportAsSvg(shapes:List[Node], width:Double,height:Double): Try[Unit] = {
    val chooser = Dialogs.newSvgFileChooser()
    chooser.setTitle(fontBundle.getString("export.svg"))
    val fileTry = Option(chooser.showSaveDialog(owner)) match {
      case Some(x) => Success(x)
      case _ => Failure(UserInputException("Select a file for export!"))
    }
    for{
      file <- fileTry
      path = Paths.get(file.toURI)
      } yield {
      val generator = new SvgCodeGenerator
      val str = generator.generatePrettyPrinted(shapes, width, height)
      generator.writeToFile(str)(path)
    }
  }

  /** Exports the given Icon represented by
    * the given shapes and width,height into an user-selected png-file
    */
  def exportAsBitmap(root:Node): Try[Unit] = {
   val chooser = Dialogs.newPngFileChooser()
    chooser.setTitle(fontBundle.getString("export.jpg"))
    val fileTry = Option(chooser.showSaveDialog(owner)) match {
      case Some(x) => Success(x)
      case _ => Failure(UserInputException("Select a file for export!"))
    }
    for {
      file <- fileTry
      image = root.snapshot(null, null)
      filename = file.getName
      suffix = filename.substring(filename.lastIndexOf(".")+1)
      _ <- Try (ImageIO.write(SwingFXUtils.fromFXImage(image, null), suffix, file))
    } yield ()
  }

  /** Lets the user pick an image and returns the URI of the selected file */
  def openImage: Option[URI] = {
    val chooser = Dialogs.newBitmapFileChooser()
    chooser.setTitle(fontBundle.getString("open.image"))
    val fileOp = Option(chooser.showOpenDialog(owner))
    fileOp map { file =>
      file.toURI
    }
  }
}
