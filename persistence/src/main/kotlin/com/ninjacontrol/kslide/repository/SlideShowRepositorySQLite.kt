package com.ninjacontrol.kslide.repository

import com.ninjacontrol.kslide.model.SlideShowState
import java.io.ByteArrayOutputStream
import java.sql.DriverManager
import java.time.Instant
import java.util.UUID

/**
 * SQLite implementation of the SlideShowRepository interface.
 */
class SlideShowRepositorySQLite(
    private val dbUrl: String = "jdbc:sqlite:slideshow.db",
) : SlideShowRepository {
    
    init {
        initialize()
    }
    
    /**
     * Initializes the database by creating the necessary tables.
     */
    private fun initialize() {
        DriverManager.getConnection(dbUrl).use { connection ->
            val statement = connection.createStatement()
            statement.execute(
                """
                CREATE TABLE IF NOT EXISTS slideshow (
                    id TEXT PRIMARY KEY,
                    filename TEXT,
                    title TEXT,
                    created_at TIMESTAMP,
                    author TEXT,
                    slide_data BLOB
                )
                """,
            )
        }
    }

    override fun add(slideShow: SlideShowState) {
        DriverManager.getConnection(dbUrl).use { connection ->
            val sql = """
                INSERT OR REPLACE INTO slideshow (id, filename, title, created_at, author, slide_data)
                VALUES (?, ?, ?, ?, ?, ?)
            """
            connection.prepareStatement(sql).use { statement ->
                // Extract presentation as byte array
                val baos = ByteArrayOutputStream()
                slideShow.ppt.write(baos)
                val slideData = baos.toByteArray()

                // Store in database
                statement.setString(1, slideShow.id.toString())
                statement.setString(2, slideShow.filename)
                statement.setString(3, slideShow.title)
                statement.setLong(4, slideShow.createdAt.toEpochMilli())
                statement.setString(5, slideShow.author)
                statement.setBytes(6, slideData)

                statement.executeUpdate()
            }
        }
    }

    override fun get(id: UUID): SlideShowState? {
        DriverManager.getConnection(dbUrl).use { connection ->
            val sql = "SELECT * FROM slideshow WHERE id = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, id.toString())
                val resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    val filename = resultSet.getString("filename")
                    val title = resultSet.getString("title")
                    val createdAt = Instant.ofEpochMilli(resultSet.getLong("created_at"))
                    val author = resultSet.getString("author")
                    val slideData = resultSet.getBytes("slide_data")

                    return SlideShowState(
                        id = id,
                        filename = filename,
                        title = title,
                        createdAt = createdAt,
                        author = author,
                        slideShowBytes = slideData,
                    )
                }
            }
        }
        return null
    }

    override fun remove(id: UUID) {
        DriverManager.getConnection(dbUrl).use { connection ->
            val sql = "DELETE FROM slideshow WHERE id = ?"
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, id.toString())
                statement.executeUpdate()
            }
        }
    }
}