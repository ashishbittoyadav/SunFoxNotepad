package com.example.sunfoxnotepad.phoneauthentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.sunfoxnotepad.R
import com.example.sunfoxnotepad.databinding.ActivityPhoneAuthBinding
import com.example.sunfoxnotepad.ui.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhoneAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhoneAuthBinding
    private lateinit var verificationId:String
    private lateinit var mAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_phone_auth)

        mAuth = FirebaseAuth.getInstance()
        mAuth.setLanguageCode("in")
        binding.buttonSms.setOnClickListener {
            verifyMobileNumber(binding.editTextPhone.text.toString())
        }

        binding.buttonVerify.setOnClickListener {
            verifyCode(binding.editTextSms.toString())
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId,code)
        signInWithPhoneAuthCredential(credential)
    }

    private fun verifyMobileNumber(phoneNumber: String){
        Log.d("firebase",phoneNumber)
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val code = p0.smsCode
                    if(code!=null){
                        verifyCode(code!!)
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    Log.d("firebase",p0.toString())
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    verificationId=p0
                }

            }) // OnVerificationStateChangedCallbacks
    }

    private fun signInWithPhoneAuthCredential(p0: PhoneAuthCredential) {
        mAuth.signInWithCredential(p0)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val user = it.result?.user
                    Log.d("firebaseUser",user?.phoneNumber.toString()+" "+user?.metadata.toString())
                    val intent = Intent(this,MainActivity::class.java)
                    intent.putExtra("user",it.result?.user?.phoneNumber.toString())
                    startActivity(intent)
                }
            }
    }
}