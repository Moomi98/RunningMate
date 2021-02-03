package org.techtown.runningmate

import android.app.Activity
import io.realm.Realm
import io.realm.RealmConfiguration

class MyRealm {
    companion object{
        lateinit var realm : Realm
        fun initRealm(activity : Activity){
            Realm.init(activity)
            val config = RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build()
            Realm.setDefaultConfiguration(config)
            realm = Realm.getDefaultInstance() // 여기 까지가 기본 작업

        }
    }
}