/**
 * Copyright (C) 2016 Nicola Justus <nicola.justus@mni.thm.de>
 * 
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package de.thm.move.controllers

import de.thm.move.Global
import de.thm.move.history.History
import de.thm.move.types._
import de.thm.move.util.JFxUtils._
import de.thm.move.views.SelectionGroup
import de.thm.move.views.panes.SnapLike.Snapable
import de.thm.move.views.panes.{SnapLike, SnapPointLike}
import de.thm.move.views.shapes.MovableShape
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.Node
import javafx.scene.input.MouseEvent

/** Behaviour for moving selected ResizableShapes. */
trait SelectedMoveCtrl extends SnapPointLike{
  this: SelectionCtrlLike =>

  def getSnapLike:SnapLike

  var Like:ChangeDrawPanelLike
  val snapToPointProperty = new SimpleBooleanProperty(false)

  def getMoveHandler: MouseEvent => Unit = {
    var mouseP = (0.0,0.0)
    var startP = mouseP

    def moveElement(mv: MouseEvent): Unit =
      (mv.getEventType, mv.getSource) match {
        case (MouseEvent.MOUSE_PRESSED, node: Node with MovableShape) =>
          mouseP = (mv.getSceneX,mv.getSceneY)
          startP = mouseP //save start-point for undo
          node match {
            case node: Snapable =>
              SnapLike.beforeSnap(getSnapLike, node, (mv.getX, mv.getY)) // prepare snap-to-grid operation
            case _ => //Ignored
          }
        case (MouseEvent.MOUSE_DRAGGED, node: Node with MovableShape) =>
          //translate from original to new position
          val delta = (mv.getSceneX - mouseP.x, mv.getSceneY - mouseP.y)
          // if clicked shape is in selection:
          // move all selected
          // else: move only clicked shape
          withParentMovableElement(node) { shape =>
            val allShapes =
              if(getSelectedShapes.contains(shape)) getSelectedShapes
              else List(shape)

            allShapes.foreach(_.move(delta))

            node match {
              case node: Snapable =>
                SnapLike.applySnapToGrid (getSnapLike, node, (mv.getX, mv.getY) ) // Apply snap-to-grid
              case _ => //Ignored
            }

            //don't forget to use the new mouse-point as starting-point
            mouseP = (mv.getSceneX,mv.getSceneY)
          }
        case (MouseEvent.MOUSE_RELEASED, group: SelectionGroup) =>
          if (snapToPointProperty.get)
            snapPointGroup(group)
        case (MouseEvent.MOUSE_RELEASED, node: Node with MovableShape) =>
          withParentMovableElement(node) { shape =>
            val movedShapes =
              if(getSelectedShapes.contains(shape)) getSelectedShapes
              else List(shape)

            node match {
              case node: Snapable => {
                SnapLike.afterSnap() // complete snap-to-grid operation

                //If the snap to point mode is activated, snap given node to the nearest point.
                if (snapToPointProperty.get)
                  snapPointShape(node)
              }
              case _ => //Ignored
            }

            //calculate delta (offset from original position) for un-/redo
            val deltaRedo = (mv.getSceneX - startP.x, mv.getSceneY - startP.y)
            val deltaUndo = deltaRedo.map(_*(-1))
            val cmd = History.
              newCommand(
                movedShapes.foreach(_.move(deltaRedo)),
                movedShapes.foreach(_.move(deltaUndo))
              )
            Global.history.save(cmd)
          }
        case _ => //unknown event
      }
    //Give back the function you just defined.
    moveElement
  }

  def move(p:Point): Unit = getSelectedShapes.foreach(_.move(p))
}
