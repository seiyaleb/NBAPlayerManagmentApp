package com.nbaplayermanagmentapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.nbaplayermanagmentapp.async.AsyncDetail;
import com.nbaplayermanagmentapp.async.AsyncUpdate;
import com.nbaplayermanagmentapp.database.AppDatabase;
import com.nbaplayermanagmentapp.database.AppDatabaseSingleton;
import com.nbaplayermanagmentapp.viewmodel.IdViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private AppDatabase db;
    private EditText et_player;
    private Spinner spinner;
    private EditText et_memo;
    private Button bt_update;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FormFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //スピナー表示設定
        spinner = view.findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),R.array.position_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //データベースを呼び出し
        db = AppDatabaseSingleton.getInstance(getActivity());

        et_player = view.findViewById(R.id.et_player);
        et_memo = view.findViewById(R.id.et_memo);
        bt_update = view.findViewById(R.id.bt_update);

    }

    @Override
    public void onResume() {
        super.onResume();

        //ViewModelからid取得
        IdViewModel viewModel = new ViewModelProvider(requireActivity()).get(IdViewModel.class);
        int id = viewModel.getId().getValue().intValue();

        //非同期処理（詳細表示）
        AsyncDetail asyncDetail = new AsyncDetail(db,id,et_player,spinner,et_memo);
        asyncDetail.asyncExecute();

        //更新ボタンを選択した場合
        bt_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //入力された選手名、ポジション、メモを取得
                String player = et_player.getText().toString();
                String position = (String) spinner.getSelectedItem();
                String memo = et_memo.getText().toString();

                //非同期処理（更新）
                AsyncUpdate asyncUpdate = new AsyncUpdate(db,id,player,position,memo);
                asyncUpdate.asyncExecute();

                //選手一覧画面へ戻る
                getActivity().finish();
            }
        });
    }
}