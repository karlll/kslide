package com.ninjacontrol.kslide.util

import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFSlide
import java.awt.AlphaComposite
import java.awt.RenderingHints
import java.awt.geom.Dimension2D
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO

object RenderSlide {
    /**
     * Renders the specified slide (zero-based index) of the given XMLSlideShow to a PNG file.
     *
     * @param ppt           The XMLSlideShow instance.
     * @param slideIndex    Zero-based index of the slide to render.
     * @param outFile       The output PNG file.
     * @param scale         The scale factor (e.g., 2.0 for double size).
     * @param renderingHints (Optional) Additional RenderingHints for Graphics2D (nullable).
     * @throws IOException  If an error occurs during writing the PNG.
     * @throws IndexOutOfBoundsException If the slide index is invalid.
     */
    @JvmStatic
    @Throws(IOException::class, IndexOutOfBoundsException::class)
    fun renderSlideToPNG(
        ppt: XMLSlideShow,
        slideIndex: Int,
        outFile: File,
        scale: Double = 1.0,
        renderingHints: Map<RenderingHints.Key, Any>? = null,
    ) {
        val img = renderSlideToImage(ppt, slideIndex, scale, renderingHints)
        if (!ImageIO.write(img, "png", outFile)) {
            throw IOException("Failed to write PNG file: ${outFile.absolutePath}")
        }
    }

    /**
     * Returns an InputStream of the PNG bytes for the specified slide.
     *
     * @param ppt           The XMLSlideShow instance.
     * @param slideIndex    Zero-based index of the slide to render.
     * @param scale         The scale factor (e.g., 2.0 for double size).
     * @param renderingHints (Optional) Additional RenderingHints for Graphics2D (nullable).
     * @return InputStream containing the PNG data. (Caller is responsible for closing the stream.)
     * @throws IOException  If an error occurs during PNG encoding.
     * @throws IndexOutOfBoundsException If the slide index is invalid.
     */
    @JvmStatic
    @Throws(IOException::class, IndexOutOfBoundsException::class)
    fun getPNGStreamForSlide(
        ppt: XMLSlideShow,
        slideIndex: Int,
        scale: Double = 1.0,
        renderingHints: Map<RenderingHints.Key, Any>? = null,
    ): InputStream {
        val img = renderSlideToImage(ppt, slideIndex, scale, renderingHints)
        val baos = ByteArrayOutputStream()
        if (!ImageIO.write(img, "png", baos)) {
            throw IOException("Failed to encode slide PNG to stream")
        }
        return ByteArrayInputStream(baos.toByteArray())
    }

    /**
     * Renders the slide to a BufferedImage. Internal helper.
     */
    private fun renderSlideToImage(
        ppt: XMLSlideShow,
        slideIndex: Int,
        scale: Double = 1.0,
        renderingHints: Map<RenderingHints.Key, Any>? = null,
    ): BufferedImage {
        requireNotNull(ppt) { "XMLSlideShow must not be null" }
        require(scale > 0) { "Scale must be positive" }
        requireNotNull(ppt.slides[slideIndex]) { "Invalid slide index: $slideIndex" }

        val slide: XSLFSlide = ppt.slides[slideIndex]
        val pgsize: Dimension2D = ppt.pageSize
        val width = kotlin.math.ceil(pgsize.width * scale).toInt()
        val height = kotlin.math.ceil(pgsize.height * scale).toInt()

        val img = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val graphics = img.createGraphics()

        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
        renderingHints?.let { graphics.addRenderingHints(it) }

        graphics.composite = AlphaComposite.Clear
        graphics.fillRect(0, 0, width, height)
        graphics.composite = AlphaComposite.SrcOver

        graphics.scale(scale, scale)
        slide.draw(graphics)
        graphics.dispose()
        return img
    }

    /**
     * Convenience overload with default scale = 1.0 and no custom rendering hints.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun renderSlideToPNG(
        ppt: XMLSlideShow,
        slideIndex: Int,
        outFile: File,
    ) {
        renderSlideToPNG(ppt, slideIndex, outFile, 1.0, null)
    }

    /**
     * Convenience overload with custom scale and no custom rendering hints.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun renderSlideToPNG(
        ppt: XMLSlideShow,
        slideIndex: Int,
        outFile: File,
        scale: Double,
    ) {
        renderSlideToPNG(ppt, slideIndex, outFile, scale, null)
    }
}
