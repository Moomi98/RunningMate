package org.techtown.runningmate

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import org.techtown.runningmate.databinding.DeletedialogBinding

class DeleteDialog(private val activity : Activity){
    private lateinit var deleteDialog: DeletedialogBinding
    private lateinit var dialogListener : AddOnDeleteDialogListener
    private val myDialogBuilder = AlertDialog.Builder(activity)
    private val myDialog = myDialogBuilder.create()

    fun startDialog() {
        Log.d("dialog", "startDialog")
        deleteDialog = DeletedialogBinding.inflate(activity.layoutInflater)
        myDialog.setView(deleteDialog.root)
        myDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        myDialog.setCancelable(false)
        myDialog.show()
        setDialogButton()

    }

    fun setOnDeleteDialogListener(listener : AddOnDeleteDialogListener){
        dialogListener = listener
    }

    private fun setDialogButton(){
        deleteDialog.finishDialogYesText.setOnClickListener { // '예' 를 눌렀을 때
            dialogListener.onYesClicked() // RunningResult 의 onYesClicked 리스너 호출
            myDialog.dismiss()
        }

        deleteDialog.finishDialogNoText.setOnClickListener { // '아니요' 를 눌렀을 때
            myDialog.dismiss()
        }
    }

    interface AddOnDeleteDialogListener{ // 리스너 구현을 위한 인터페이스
        fun onYesClicked() //dialog 에서 '예' 버튼을 누를시 동작되는 콜백 메소드
    }
}