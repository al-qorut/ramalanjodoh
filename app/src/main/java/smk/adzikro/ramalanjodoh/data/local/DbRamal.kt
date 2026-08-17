package smk.adzikro.ramalanjodoh.data.local

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import smk.adzikro.ramalanjodoh.data.models.Ramal
import smk.adzikro.ramalanjodoh.data.models.RamalDao
import smk.adzikro.ramalanjodoh.data.models.UserDao
import smk.adzikro.ramalanjodoh.data.models.Userx

@Database(entities = [Ramal::class, Userx::class],
    version = 3,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3)],

    exportSchema = true
)
abstract class DbRamal : RoomDatabase() {

    abstract fun ramal():RamalDao
    abstract fun user(): UserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
            CREATE TABLE IF NOT EXISTS `user` (
                `uid` TEXT PRIMARY KEY NOT NULL,
                `displayName` TEXT NOT NULL,
                `email` TEXT NOT NULL,
                `token` INTEGER NOT NULL DEFAULT 0)
        """)
            }
        }

        private var INSTANCE: DbRamal? = null
        fun getInstance(context: Context): DbRamal? {
            if (INSTANCE == null) {
                synchronized(DbRamal::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, DbRamal::class.java, "dataramal.db"
                    )
                        .addMigrations(MIGRATION_1_2).build()
                }
            }
            return INSTANCE
        }
    }
}