package ir.parsgeeks.parkingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(), LoginPresenter.View {


    val presenter: LoginPresenter = LoginPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        presenter.attachView(this)

        loginBtn.setOnClickListener {
            if (username.text.toString().isNotEmpty())
                presenter.loginInfo(applicationContext,username.text.toString())
            else
                Toast.makeText(applicationContext,"Username is empty",Toast.LENGTH_LONG).show()
        }
    }


    override fun loginSuccessful() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}
