package com.vietmobi.mobile.audiovideocutter.ui.screen.save;

import android.view.View;
import android.widget.Toast;

import com.vietmobi.mobile.audiovideocutter.R;
import com.vietmobi.mobile.audiovideocutter.base.BaseDialog;
import com.vietmobi.mobile.audiovideocutter.databinding.FragmentSaveDialogBinding;
import com.vietmobi.mobile.audiovideocutter.ui.utils.EmptyUtil;

public class SaveDialogFragment extends BaseDialog<FragmentSaveDialogBinding>
        implements View.OnClickListener {

    @Override
    protected void initView() {
        binding.btnCancle.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
    }

    @Override
    public boolean isFullScreen() {
        return true;
    }

    @Override
    protected int idLayoutRes() {
        return R.layout.fragment_save_dialog;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getStyleDialog() {
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancle:
                dissmissDialog();
                break;
            case R.id.btn_save:
                String name = binding.edtName.getText().toString().trim();
                if (EmptyUtil.isNotEmpty(name)) {

                } else {
                    Toast.makeText(getBaseActivity(), getString(R.string.text_error_name_is_not_empty)
                            , Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
