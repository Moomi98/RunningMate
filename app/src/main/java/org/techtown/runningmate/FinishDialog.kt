package org.techtown.runningmate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.techtown.runningmate.databinding.StartrunningFinishdialogBinding

class FinishDialog : AppCompatActivity() {
    private lateinit var finishDialog: StartrunningFinishdialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        finishDialog = StartrunningFinishdialogBinding.inflate(layoutInflater)
        setContentView(finishDialog.root)
    }

    private fun setButton(){
        finishDialog.finishDialogYesText.setOnClickListener {

        }
    }
}