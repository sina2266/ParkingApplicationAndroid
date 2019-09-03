package ir.parsgeeks.parkingapp

import android.content.Context

class LoginPresenter : BasePresenter<LoginPresenter.View>() {

    fun loginInfo(context: Context,username : String){
        SharedPreferencesHelper.getUserData(context).username = username
        view?.loginSuccessful()
    }

    interface View {
        fun loginSuccessful()
    }
}