package org.techtown.runningmate

import io.realm.RealmList
import io.realm.RealmObject

open class UserData : RealmObject(){
    var name : String = ""
    var totalDistance : Double = 0.0
    var totalRunningTime : String = "00:00"
}

open class RunningList : RealmObject() {
    var date: String = ""
    var runningDay : RunningOfDay = RunningOfDay()
}

open class RunningOfDay : RealmObject() {
    var year: Int = 0 // 년
    var month: Int = 0 // 월
    var day: Int = 0 // 일
    var runningTime : String = "" // 하루 달린 시간
    var distance : Int = 0 // 하루 달린 거리
    var pace : String = "" // 하루 동안 페이스
    var kcal : Double = 0.0 // 하루 동안 소모한 칼로리
}