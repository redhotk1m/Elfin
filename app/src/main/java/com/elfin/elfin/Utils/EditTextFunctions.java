package com.elfin.elfin.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.elfin.elfin.MainActivity;

public class EditTextFunctions {

    MainActivity mainActivity;
    EditText editText;
    ListView listViewSuggest;
    TextView destinacionTextView;
    Boolean isSelected;
    int temp = 0;

    /**
     * Class for setting the different functionalities for editText --> focuse changed, textchanged
     * When the edittext is bigger than 3 letters is calles the displaySuggestion method from main.
     * For every letter bigger than 3 it runs the method to display tje mew suggestions based on your search.
     *
     */

    public EditTextFunctions(MainActivity mainActivity, boolean isSelected){
        this.mainActivity=mainActivity;
        editText=mainActivity.editText;
        listViewSuggest=mainActivity.listViewSuggest;
        destinacionTextView=mainActivity.destinacionTextView;
        this.isSelected = isSelected;
    }



    public void setText(){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {


                if(editable.toString().length()>= 1){
                    destinacionTextView.setVisibility(View.INVISIBLE);
                }

                if(editable.toString().length()<=3){
                    listViewSuggest.setVisibility(View.INVISIBLE);
                }


                if(editable.toString().length() >= 3){
                    mainActivity.displaySuggestions(editable.toString());
                }


            }
        });


        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    listViewSuggest.setVisibility(View.INVISIBLE);
                    mainActivity.closeKeyboard(view);
                } else {
                    isSelected=false;
                }
            }
        });



    }

}
