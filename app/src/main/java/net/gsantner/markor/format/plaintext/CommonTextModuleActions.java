/*#######################################################
 *
 *   Maintained by Gregor Santner, 2018-
 *   https://gsantner.net/
 *
 *   License of this file: Apache 2.0 (Commercial upon request)
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
#########################################################*/
package net.gsantner.markor.format.plaintext;

import android.app.Activity;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import net.gsantner.markor.R;
import net.gsantner.markor.model.Document;
import net.gsantner.markor.ui.SearchOrCustomTextDialogCreator;
import net.gsantner.markor.ui.hleditor.HighlightingEditor;
import net.gsantner.markor.util.DownloadImageTask;
import net.gsantner.opoc.format.plaintext.PlainTextStuff;
import net.gsantner.opoc.util.ContextUtils;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class CommonTextModuleActions {
    public static final int ACTION_SPECIAL_KEY__ICON = R.drawable.ic_keyboard_black_24dp;
    public static final String ACTION_SPECIAL_KEY = "press_special_key";

    public static final int ACTION_OPEN_LINK_BROWSER__ICON = R.drawable.ic_open_in_browser_black_24dp;
    public static final String ACTION_OPEN_LINK_BROWSER = "open_selected_link_in_browser";


    public static final int ACTION_DELETE_LINES_ICON = R.drawable.ic_delete_black_24dp;
    public static final String ACTION_DELETE_LINES = "delete_lines_between";


    public static final int ACTION_END_LINE_WITH_TWO_SPACES_ICON = R.drawable.ic_keyboard_return_black_24dp;
    public static final String ACTION_END_LINE_WITH_TWO_SPACES = "markdown_end_line_with_two_spaces";

    public static final int ACTION_SEARCH_ICON = R.drawable.ic_search_black_24dp;
    public static final String ACTION_SEARCH = "search_in_content";


    public static final int ACTION_JUMP_BOTTOM_TOP_ICON = R.drawable.format_header_1;
    public static final String ACTION_JUMP_BOTTOM_TOP = "jump_bottom_top";


    private final Activity _activity;
    private final Document _document;
    private final HighlightingEditor _hlEditor;

    public CommonTextModuleActions(Activity activity, Document document, HighlightingEditor hlEditor) {
        _activity = activity;
        _document = document;
        _hlEditor = hlEditor;
    }

    private String rstr(@StringRes int resKey) {
        return _activity.getString(resKey);
    }

    // Returns true when handled
    public boolean runAction(String action) {
        final String origText = _hlEditor.getText().toString();
        switch (action) {
            case ACTION_SPECIAL_KEY: {
                SearchOrCustomTextDialogCreator.showSpecialKeyDialog(_activity, (callbackPayload) -> {
                    if (!_hlEditor.hasSelection() && _hlEditor.length() > 0) {
                        _hlEditor.requestFocus();
                    }
                    if (callbackPayload.equals(rstr(R.string.key_page_down))) {
                        _hlEditor.simulateKeyPress(KeyEvent.KEYCODE_PAGE_DOWN);
                    } else if (callbackPayload.equals(rstr(R.string.key_page_up))) {
                        _hlEditor.simulateKeyPress(KeyEvent.KEYCODE_PAGE_UP);
                    } else if (callbackPayload.equals(rstr(R.string.key_pos_1))) {
                        _hlEditor.simulateKeyPress(KeyEvent.KEYCODE_MOVE_HOME);
                    } else if (callbackPayload.equals(rstr(R.string.key_pos_end))) {
                        _hlEditor.simulateKeyPress(KeyEvent.KEYCODE_MOVE_END);
                    } else if (callbackPayload.equals(rstr(R.string.key_pos_1_document))) {
                        _hlEditor.setSelection(0);
                    } else if (callbackPayload.equals(rstr(R.string.key_pos_end_document))) {
                        _hlEditor.setSelection(_hlEditor.length());
                    } else if (callbackPayload.equals(rstr(R.string.key_ctrl_a))) {
                        _hlEditor.setSelection(0, _hlEditor.length());
                    } else if (callbackPayload.equals(rstr(R.string.key_tab))) {
                        _hlEditor.insertOrReplaceTextOnCursor("\u0009");
                    } else if (callbackPayload.equals(rstr(R.string.search))) {
                        runAction(ACTION_SEARCH);
                    }
                });
                return true;
            }
            case ACTION_OPEN_LINK_BROWSER: {

                //NEW METHODS
                String url;
                if ((url = PlainTextStuff.tryExtractUrlAroundPos(_hlEditor.getText().toString(), _hlEditor.getSelectionStart())) != null) {
                    if (url.endsWith(")")) {
                        url = url.substring(0, url.length() - 1);
                    }
                    if(url.contains(".jpg")|| url.contains(".png")|| url.contains(".jpeg")) {
                        // inflate the layout of the popup window
                        LayoutInflater inflater = (LayoutInflater) _activity.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View popupView = inflater.inflate(R.layout.popup_window, (ViewGroup) ((ViewGroup) this._activity.findViewById(android.R.id.content)).getChildAt(0), false);

                        // create the popup window
                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        boolean focusable = true; // lets taps outside the popup also dismiss it
                        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                        // show the popup window
                        // which view you pass in doesn't matter, it is only used for the window tolken
                        popupWindow.showAtLocation(((ViewGroup) this._activity.findViewById(android.R.id.content)).getChildAt(0), Gravity.CENTER, 0, 0);
                        // dismiss the popup window when touched
                        popupView.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                popupWindow.dismiss();
                                return true;
                            }
                        });
                        ImageView imageview = popupView.findViewById(R.id.imageview_url);
                        if (imageview != null) {
                            new DownloadImageTask((ImageView) imageview).execute(url);
                        }
                    }
                    else {
                        Toast.makeText(_activity.getBaseContext(), "This link does not contain any image to show", Toast.LENGTH_LONG).show();
                        return true;
                    }
                }
                return true;
            }
            case ACTION_DELETE_LINES: {
                int[] indexes = PlainTextStuff.getNeighbourLineEndings(_hlEditor.getText().toString(), _hlEditor.getSelectionStart(), _hlEditor.getSelectionEnd());
                if (indexes != null) {
                    _hlEditor.getText().delete(indexes[0], indexes[1]);
                }
                return true;
            }
            case ACTION_END_LINE_WITH_TWO_SPACES: {
                if (_hlEditor.length() > 1) {
                    int start = _hlEditor.getSelectionStart();
                    String text = _hlEditor.getText().toString();
                    int insertPos = text.indexOf('\n', start);
                    insertPos = insertPos < 1 ? text.length() : insertPos;
                    _hlEditor.getText().insert(insertPos, "  " + (text.endsWith("\n") ? "" : "\n"));
                    _hlEditor.setSelection(((insertPos + 3) > _hlEditor.length() ? _hlEditor.length() : (insertPos + 3)));
                }
                return true;
            }
            case ACTION_SEARCH: {
                SearchOrCustomTextDialogCreator.showSearchDialog(_activity, origText, callbackPayload -> {
                    int cursor = origText.indexOf(callbackPayload);
                    _hlEditor.setSelection(Math.min(_hlEditor.length(), Math.max(0, cursor)));
                });
                return true;
            }

            case ACTION_JUMP_BOTTOM_TOP: {
                int pos = _hlEditor.getSelectionStart();
                _hlEditor.setSelection(pos == 0 ? _hlEditor.getText().length() : 0);
                return true;
            }
        }
        return false;
    }
}
