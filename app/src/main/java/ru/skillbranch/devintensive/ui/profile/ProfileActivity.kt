package ru.skillbranch.devintensive.ui.profile

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.models.Bender
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.utils.Utils
import ru.skillbranch.devintensive.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private val stId = 10

    companion object{
        const val IS_EDIT_MODE = "IS_EDIT_MODE"
    }

    private lateinit var viewModel: ProfileViewModel
    var isEditMode = false
    lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO set custom Theme this before super and setContentView

        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews(savedInstanceState)
        initViewModel()

        Log.d("M_ProfileActivity","onCreate")

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(IS_EDIT_MODE, isEditMode)
    }

    private fun initViewModel(){
        viewModel = ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        viewModel.getProfileData().observe(this, Observer { updateUI(it) })
        viewModel.getTheme().observe(this, Observer { updateTheme(it) })
        viewModel.getIsRepoError().observe(this, Observer { updateRepository(it) })
    }

    private fun updateRepository(isError: Boolean) {
        if (isError) et_repository.text.clear()
    }

    private fun updateTheme(mode: Int) {
        Log.d("M_ProfileActivity","updateTheme")
        delegate.setLocalNightMode(mode)
    }

    private fun updateUI(profile: Profile) {
        profile.toMap().also {
            for ((k,v) in viewFields) {
                v.text = it[k].toString()
            }
        }
        updateAvatar(profile)
    }

    private fun updateAvatar(profile: Profile) {
        Utils.toInitials(profile.firstName, profile.lastName)?.let {
            if (it != null) {
                val color = TypedValue()
                theme.resolveAttribute(R.attr.colorAccent, color, true)
                with(iv_avatar) {
                    setInitials(it)
                    setBgColor(color.data)
                }
            }
        } ?: iv_avatar.setImageResource(R.drawable.avatar_default)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
            "nickname" to tv_nick_name,
            "rank" to tv_rank,
            "firstName" to et_first_name,
            "lastName" to et_last_name,
            "about" to et_about,
            "repository" to et_repository,
            "rating" to tv_rating,
            "respect" to tv_respect
        )

        isEditMode = savedInstanceState?.getBoolean(IS_EDIT_MODE, false) ?: false
        showCurrentMode(isEditMode)

        btn_edit.setOnClickListener{
            viewModel.onRepoEditCompleted(wr_repository.isErrorEnabled)

            if (isEditMode) saveProfileInfo()
            isEditMode = !isEditMode
            showCurrentMode(isEditMode)
        }

        btn_switch_theme.setOnClickListener {
            viewModel.switchTheme()
        }
    }

    private fun showCurrentMode(isEdit: Boolean) {
        val info = viewFields.filter { setOf("firstName","lastName","about","repository").contains(it.key) }
        for ((_,v) in info) {
            v as EditText
            v.isFocusable = isEdit
            v.isFocusableInTouchMode = isEdit
            v.isEnabled = isEdit
            v.background.alpha = if (isEdit) 255 else 0
        }

        ic_eye.visibility=if (isEdit) View.GONE else View.VISIBLE
        wr_about.isCounterEnabled = isEdit

        with(btn_edit){
            val filter: ColorFilter? = if(isEdit) {
                PorterDuffColorFilter(
                    resources.getColor(R.color.color_accent, theme),
                    PorterDuff.Mode.SRC_IN
                )
            } else null

            val icon = if(isEdit) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            }
            else resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)

            background.colorFilter = filter
            setImageDrawable(icon)
        }

        if (isEdit) validateGithubUserUrl()
        else {
            if (wr_repository.isErrorEnabled) {
                et_repository.setText("")
                wr_repository.isErrorEnabled = false
            }
        }
    }

    private fun validateGithubUserUrl() {
        et_repository.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (et_repository.text.toString().contains("^(http://|https://|)(www[\\.]|)github[\\.]com/\\w{1,38}(\\w{0,38})$".toRegex()) &&
                    et_repository.text.toString().contains("^(http://|https://|)(www[\\.]|)github[\\.]com/.{1,39}".toRegex()) &&
                    et_repository.text.toString().contains("^(http://|https://|)(www[\\.]|)github[\\.]com/(enterprise|features|topics|collections|trending|events|marketplace|pricing|nonprofit|customer-stories|security|login|join)$".toRegex()).not()){
                    wr_repository.isErrorEnabled = false
                } else {
                    wr_repository.isErrorEnabled = true
                    wr_repository.setError("Невалидный адрес репозитория")
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun saveProfileInfo() {
        Profile(
            firstName = et_first_name.text.toString(),
            lastName = et_last_name.text.toString(),
            about = et_about.text.toString(),
            repository = et_repository.text.toString()
        ).apply {
            viewModel.saveProfileDate(this)
        }
    }

}