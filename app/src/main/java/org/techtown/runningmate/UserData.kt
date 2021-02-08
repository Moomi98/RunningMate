package org.techtown.runningmate

import io.realm.RealmList
import io.realm.RealmObject

open class LoginInfo : RealmObject(){
    var userId : String = ""
    var logined : Boolean = false
}
open class UserData : RealmObject(){
    var name : String = ""
    var totalDistance : Double = 0.0
    var totalRunningTime : String = "00:00"
}

open class RunningList : RealmObject() {
    var date: String = ""
    var runningDayList : RealmList<RunningOfDay> = RealmList()
}

open class RunningOfDay : RealmObject() {

    var min : Int = 0
    var sec : Int = 0
    var distance : Double = 0.0 // 활동 당 거리
    var pace : String = "" // 활동 당 페이스
    var kcal : Double = 0.0 // 활동 당 소모한 칼로리
    var memo : String = ""
}