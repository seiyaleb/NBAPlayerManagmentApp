package com.nbaplayermanagmentapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class IdViewModel extends ViewModel {

    private MutableLiveData<Integer> id;

    public MutableLiveData<Integer> getId() {

        if(id == null) {

            id = new MutableLiveData<Integer>();
        }

        return id;
    }
}
