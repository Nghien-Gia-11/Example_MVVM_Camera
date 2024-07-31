package com.example.example_mvvm_camera

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ViewQR(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    private val paintCenter = Paint().apply {
        setAlpha(0)

        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
        isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Calculate the coordinates for the square
        val leftCenter = (width / 2f) - 300f
        val topCenter = (height / 2f) - 300f
        val rightCenter = (width / 2f) + 300f
        val bottomCenter = (height / 2f) + 300f

        // Draw the square
        canvas.drawRect(leftCenter, topCenter, rightCenter, bottomCenter, paintCenter)


        // Square side length
        val sideLength = 700f
        val cornerRadius = 20f

        // Calculate the coordinates for the square
        val left = (width / 2f) - (sideLength / 2)
        val top = (height / 2f) - (sideLength / 2)
        val right = (width / 2f) + (sideLength / 2)
        val bottom = (height / 2f) + (sideLength / 2)

        // Define the rectangle bounds for each corner arc
        val topLeftRect = RectF(left, top, left + 2 * cornerRadius, top + 2 * cornerRadius)
        val topRightRect = RectF(right - 2 * cornerRadius, top, right, top + 2 * cornerRadius)
        val bottomLeftRect = RectF(left, bottom - 2 * cornerRadius, left + 2 * cornerRadius, bottom)
        val bottomRightRect =
            RectF(right - 2 * cornerRadius, bottom - 2 * cornerRadius, right, bottom)

        // Draw the four rounded corners using arcs
        canvas.drawArc(topLeftRect, 180f, 90f, false, paint) // Top-left corner
        canvas.drawArc(topRightRect, 270f, 90f, false, paint) // Top-right corner
        canvas.drawArc(bottomLeftRect, 90f, 90f, false, paint) // Bottom-left corner
        canvas.drawArc(bottomRightRect, 0f, 90f, false, paint) // Bottom-right corner

    }
}