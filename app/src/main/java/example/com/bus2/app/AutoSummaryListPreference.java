package example.com.bus2.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

/**
 * Created by Arkady Gorodischer on 29/07/18.
 *
 * Used to Autofill the summary, if a value is changed.
 * Also it takes care on so the selection list will start with the already selected value
 */

public class AutoSummaryListPreference extends ListPreference {

    private SharedPreferences sharedPreferences;

    public AutoSummaryListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setData(CharSequence[] entries, CharSequence[] entryValues, int valueIndex){
        setEntries(entries);
        setEntryValues(entryValues);
        setValueIndex(valueIndex);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            setSummary(getSummary());
        }
    }

    @Override
    public CharSequence getSummary() {

        if (isEnabled()){
            int pos = findIndexOfValue(sharedPreferences.getString(getKey(),""));

            if (pos >= 0) {
                setValueIndex(pos);
                return getEntries()[pos];
            }
            else{
                return("Wrong value selected");
            }
        }
        else{
            return "Not Applicable";
        }
    }

}
