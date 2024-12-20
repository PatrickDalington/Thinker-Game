/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.cwp.game.common.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.cwp.game.R;

public class ExpandableItemPinnedMessageDialogFragment extends DialogFragment {
    private static final String KEY_GROUP_ITEM_POSITION = "group_position";
    private static final String KEY_CHILD_ITEM_POSITION = "child_position";

    public interface EventListener {
        void onNotifyExpandableItemPinnedDialogDismissed(int groupPosition, int childPosition, boolean ok);
    }

    public static ExpandableItemPinnedMessageDialogFragment newInstance(int groupPosition, int childPosition) {
        final ExpandableItemPinnedMessageDialogFragment frag = new ExpandableItemPinnedMessageDialogFragment();
        final Bundle args = new Bundle();

        args.putInt(KEY_GROUP_ITEM_POSITION, groupPosition);
        args.putInt(KEY_CHILD_ITEM_POSITION, childPosition);

        frag.setArguments(args);
        return frag;
    }

    public ExpandableItemPinnedMessageDialogFragment() {
        super();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final int groupPosition = getArguments().getInt(KEY_GROUP_ITEM_POSITION, Integer.MIN_VALUE);
        final int childPosition = getArguments().getInt(KEY_CHILD_ITEM_POSITION, Integer.MIN_VALUE);

        // for expandable list
        if (childPosition == RecyclerView.NO_POSITION) {
            builder.setMessage(getString(R.string.dialog_message_group_item_pinned, groupPosition));
        } else {
            builder.setMessage(getString(R.string.dialog_message_child_item_pinned, groupPosition, childPosition));
        }
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> notifyItemPinnedDialogDismissed(true));
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.setCancelable(true);
        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        notifyItemPinnedDialogDismissed(false);
    }

    private void notifyItemPinnedDialogDismissed(boolean ok) {
        final int groupPosition = getArguments().getInt(KEY_GROUP_ITEM_POSITION);
        final int childPosition = getArguments().getInt(KEY_CHILD_ITEM_POSITION);
        ((EventListener) requireActivity()).onNotifyExpandableItemPinnedDialogDismissed(groupPosition, childPosition, ok);
    }

}
