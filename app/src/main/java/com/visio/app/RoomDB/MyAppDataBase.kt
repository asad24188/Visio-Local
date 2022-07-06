package com.fed.fedsense.RoomDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalProject::class,LocalCollection::class], version = 4)
abstract class MyAppDataBase : RoomDatabase() {
    abstract fun cardDao(): DaoLocalProject
    abstract fun collectionDao(): DaoLocalCollecetion
}