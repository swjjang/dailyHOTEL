package com.daily.dailyhotel.screen.mydaily.coupon.register

import android.content.Context
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.daily.base.BaseDialogView
import com.twoheart.dailyhotel.R
import com.twoheart.dailyhotel.databinding.ActivityRegisterCouponDataBinding
import com.twoheart.dailyhotel.util.StringFilter

class RegisterCouponView(activity: RegisterCouponActivity, listener: RegisterCouponInterface.OnEventListener)//
    : BaseDialogView<RegisterCouponInterface.OnEventListener, ActivityRegisterCouponDataBinding>(activity, listener)
        , RegisterCouponInterface.ViewInterface, View.OnClickListener, View.OnFocusChangeListener {

    override fun setContentView(viewDataBinding: ActivityRegisterCouponDataBinding) {
        initToolbar(viewDataBinding)

        viewDataBinding.run {
            couponEditText.run {
                setDeleteButtonVisible(null)
                onFocusChangeListener = this@RegisterCouponView

                val stringFilter = StringFilter(context)
                val allowRegisterCouponFilters = arrayOfNulls<InputFilter>(2)
                allowRegisterCouponFilters[0] = stringFilter.allowRegisterCouponFilter
                allowRegisterCouponFilters[1] = InputFilter.LengthFilter(20)

                filters = allowRegisterCouponFilters

                setOnEditorActionListener { _, actionId, _ ->
                    when (actionId) {
                        EditorInfo.IME_ACTION_DONE -> {
                            registerCouponCompleteView.performClick()
                            true
                        }
                        else -> {
                            false
                        }
                    }
                }
            }

            registerCouponCompleteView.setOnClickListener(this@RegisterCouponView)
        }
    }

    override fun setToolbarTitle(title: String?) {
        viewDataBinding.toolbarView.setTitleText(title)
    }

    private fun initToolbar(viewDataBinding: ActivityRegisterCouponDataBinding) {
        viewDataBinding.toolbarView.run {
            setTitleText(R.string.actionbar_title_register_coupon)
            setOnBackClickListener { eventListener.onBackClick() }
        }
    }

    override fun onClick(v: View?) {
        v?.let {
            when (it.id) {
                R.id.registerCouponCompleteView -> {
                    val text = viewDataBinding.couponEditText.text.toString()

                    eventListener.onRegisterCouponClick(text)
                    hideKeyboard(it)
                }
            }
        }
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        v?.let {
            when (it.id) {
                R.id.couponEditText -> {
                    setFocusLabelView(viewDataBinding.couponTitleView, viewDataBinding.couponEditText, hasFocus)
                }
            }
        }
    }

    private fun showKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun setFocusLabelView(labelView: View, editText: EditText, hasFocus: Boolean) {
        if (hasFocus) {
            labelView.isActivated = false
            labelView.isSelected = true
        } else {
            if (editText.length() > 0) {
                labelView.isActivated = true
            }

            labelView.isSelected = false
        }
    }
}