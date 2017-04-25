package com.flatmates.ixion.activity;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.flatmates.ixion.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NodeSetupActivity extends AppCompatActivity {

    @BindView(R.id.edittext_node_address)
    MaterialEditText edittextNodeAddress;
    @BindView(R.id.edittext_node_password)
    MaterialEditText edittextNodePasssword;
    @BindView(R.id.button_node_submit)
    Button buttonNodeSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_node_setup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_node_submit)
    public void validatePassword() {
        String ip = edittextNodeAddress.getText().toString();
        String password = edittextNodePasssword.getText().toString();

        if (ip.contains(".") && password.length() > 8) {
            Toast.makeText(this, "Node updated", Toast.LENGTH_SHORT).show();
            PreferenceManager.getDefaultSharedPreferences(NodeSetupActivity.this)
                    .edit()
                    .putString("node", ip)
                    .apply();
        } else {
            Toast.makeText(this, "Wrong combination", Toast.LENGTH_SHORT).show();
        }

    }

}
