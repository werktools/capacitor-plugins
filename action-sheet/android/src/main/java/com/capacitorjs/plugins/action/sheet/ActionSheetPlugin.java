package com.capacitorjs.plugins.action.sheet;

import com.getcapacitor.*;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

@NativePlugin(name = "ActionSheet")
public class ActionSheetPlugin extends Plugin {
    private ActionSheet implementation = new ActionSheet();

    @PluginMethod
    public void showActions(final PluginCall call) {
        String title = call.getString("title");
        JSArray options = call.getArray("options");
        if (title == null) {
            call.reject("Must supply a title");
            return;
        }
        if (options == null) {
            call.reject("Must supply options");
            return;
        }
        if (getActivity().isFinishing()) {
            call.reject("App is finishing");
            return;
        }

        try {
            List<Object> optionsList = options.toList();
            ActionSheetOption[] actionOptions = new ActionSheetOption[optionsList.size()];
            for (int i = 0; i < optionsList.size(); i++) {
                JSObject o = JSObject.fromJSONObject((JSONObject) optionsList.get(i));
                String titleOption = o.getString("title", "");
                actionOptions[i] = new ActionSheetOption(titleOption);
            }
            implementation.setTitle(title);
            implementation.setOptions(actionOptions);
            implementation.setCancelable(false);
            implementation.setOnSelectedListener(
                index -> {
                    JSObject ret = new JSObject();
                    ret.put("index", index);
                    call.resolve(ret);
                    implementation.dismiss();
                }
            );
            implementation.show(getActivity().getSupportFragmentManager(), "capacitorModalsActionSheet");
        } catch (JSONException ex) {
            Logger.error("JSON error processing an option for showActions", ex);
            call.reject("JSON error processing an option for showActions", ex);
        }
    }
}