package com.example.finalproject.ui.home

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.adapter.StudentAdapter
import com.example.finalproject.database.entity.StudentEntity

class SwipeToDeleteHelper(
    private val adapter: StudentAdapter,
    private val onSwipedConfirmed: (StudentEntity, Int) -> Unit
) {

    fun build(): ItemTouchHelper {
        return ItemTouchHelper(createCallback())
    }

    private fun createCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                t: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val student = adapter.getStudentAt(position)
                onSwipedConfirmed(student, position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val context = itemView.context
                val density = context.resources.displayMetrics.density

                val margin = (16 * density).toInt()
                val cornerRadius = 12 * density

                GradientDrawable().apply {
                    setColor(Color.parseColor("#F44336"))
                    setCornerRadius(cornerRadius)
                    setBounds(
                        itemView.left + margin,
                        itemView.top + (margin / 2),
                        itemView.right - margin,
                        itemView.bottom - (margin / 2)
                    )
                    draw(c)
                }
                drawDeleteIcon(c, itemView, dX, density, margin)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }

    private fun drawDeleteIcon(
        c: Canvas,
        itemView: android.view.View,
        dX: Float,
        density: Float,
        margin: Int
    ) {
        val deleteIcon = ContextCompat.getDrawable(
            itemView.context,
            android.R.drawable.ic_menu_delete
        ) ?: return

        val iconSize = (24 * density).toInt()
        val iconTop = itemView.top + (itemView.height - iconSize) / 2
        val iconBottom = iconTop + iconSize

        if (dX > 0) {
            val iconLeft = itemView.left + margin + iconSize
            val iconRight = iconLeft + iconSize
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        } else {
            val iconRight = itemView.right - margin - iconSize
            val iconLeft = iconRight - iconSize
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
        }
        deleteIcon.setTint(Color.WHITE)
        deleteIcon.draw(c)
    }
}