package org.techtown.runningmate

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import org.techtown.runningmate.databinding.StartrunningFinishdialogBinding

class FinishDialog(private val activity : Activity) {
    private lateinit var finishDialog: StartrunningFinishdialogBinding
    private lateinit var dialogListener : AddOnDialogReturnListener
    private val myDialogBuilder = AlertDialog.Builder(activity)
    private val myDialog = myDialogBuilder.create()

    fun startDialog() {
        Log.d("dialog", "startDialog")
        finishDialog = StartrunningFinishdialogBinding.inflate(activity.layoutInflater)
        myDialog.setView(finishDialog.root)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.setCancelable(false)
        myDialog.show()
        setDialogButton()

    }

    fun setOnDialogReturnListener(listener : AddOnDialogReturnListener){
        dialogListener = listener
    }


    private fun setDialogButton(){
        finishDialog.finishDialogYesText.setOnClickListener { // '예' 를 눌렀을 때
            dialogListener.onYesClicked() // RunningFragment 의 onYesClicked 리스너 호출
            myDialog.dismiss()
        }

        finishDialog.finishDialogNoText.setOnClickListener { // '아니요' 를 눌렀을 때
            dialogListener.onNoClicked() // RunningFragment 의 onNoClicked 리스너 호출
            myDialog.dismiss()
        }
    }

    interface AddOnDialogReturnListener{ // 리스너 구현을 위한 인터페이스
        fun onNoClicked() // dialog 에서 '아니오 버튼을 누를시 동작되는 콜백 메소드
        fun onYesClicked() //dialog 에서 '예' 버튼을 누를시 동작되는 콜백 메소드
    }
}