package org.techtown.runningmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.get
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.techtown.runningmate.databinding.ActivityRegisterInformationBinding


class RegisterInformation : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var binding: ActivityRegisterInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = intent.getStringExtra("email")!!

        setButton()
    }

    private fun setButton() { // 버튼 설정
        binding.registerButton.setOnClickListener {
            registerInfo()
        }
    }

    private fun registerInfo() { // 별명, 생년월일, 성별 정보 저장
        val nickName : String = binding.writeNickName.text.toString()
        val birth = binding.writeBirth.text.toString()
        val gender = if(binding.manChip.isChecked){
            "남성"
        }
        else
            "여성"

        if(nickName.isEmpty() || birth.isEmpty()){
            Toast.makeText(this, "정보를 정확히 입력해주세요", Toast.LENGTH_SHORT).show()
        }
        else {
            val db = Firebase.firestore // firestore 객체 생성
            val user = hashMapOf(
                // firestore에 저장할 hashMap 생성
                "nickName" to nickName,
                "birth" to birth,
                "gender" to gender,
            )

            db.collection("userInfo").document(email).set(user)
                .addOnSuccessListener { // 해당 유저의 이메일 문서에 정보 저장
                    Toast.makeText(this, "저장 완료!", Toast.LENGTH_SHORT).show()
                    loginQuery(email)
                }.addOnCanceledListener {
                Toast.makeText(this, "저장 실패", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun loginQuery(email : String) { // 첫 사용자는 local db에 이메일을 저장하고 로그인 상태를 true 로 전환
        MyRealm.realm.executeTransaction {
            with(it.createObject(LoginInfo::class.java)) {
                this.logined = true // true 로 전환함으로서 다음번에 앱을 실행 할 때 자동 로그인
                this.userId = email
            }
        }
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}