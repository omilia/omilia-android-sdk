package com.omilia.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.State;

import com.omilia.demo.R;
import com.omilia.demo.databinding.FragmentMessageBinding;
import com.omilia.sdk.ChatListener;
import com.omilia.sdk.OmiliaClient;
import com.omilia.sdk.models.Message;
import com.omilia.sdk.models.MessageType;
import com.omilia.sdk.models.User;
import com.omilia.ui.adapters.MessagesAdapter;
import com.omilia.ui.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageFragment extends Fragment implements ChatListener {

    // constants
    private static final String TAG = MessageFragment.class.getSimpleName();

    // attributes
    private OmiliaClient omiliaClient;
    private boolean isFragmentAttached;
    private FragmentActivity mActivity;

    // state
    private String dialogId = null;
    private final User user = User.builder()
            .username("omilia")
            .build();
    // ui
    private FragmentMessageBinding binding;

    private MessagesAdapter messagesAdapter;

    /// overrides
    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
        if (context instanceof Activity) {
            mActivity = (FragmentActivity) context;
        }
        isFragmentAttached = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.binding = FragmentMessageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        omiliaClient = OmiliaClient.getInstance();
        omiliaClient.setChatListener(this);
        initUi(view);

        if (savedInstanceState == null) {
            omiliaClient.startDialog(user);
        } else { // restore state
            List<Message> messages = savedInstanceState.getParcelableArrayList("messages");
            dialogId = savedInstanceState.getString("dialogId");
            messagesAdapter.setData(messages);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("messages", (ArrayList<? extends Parcelable>) messagesAdapter.getData());
        outState.putString("dialogId", dialogId);
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
        this.binding = null;
    }

    // private
    private void initUi(View view) {

        // recycler view
        binding.messagesList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Objects.requireNonNull(getActivity()).getApplicationContext()) {

            @Override
            public void onLayoutCompleted(State state) {

                super.onLayoutCompleted(state);
                //scrolling to the right position of the RV
                final int position = messagesAdapter.getItemCount() > 0 ? messagesAdapter.getItemCount() - 1 : 0;
                binding.messagesList.smoothScrollToPosition(position);
            }
        };

        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(false);

        binding.messagesList.setLayoutManager(linearLayoutManager);

        messagesAdapter = new MessagesAdapter(view.getContext());
        binding.messagesList.setAdapter(messagesAdapter);
        binding.messagesList.getRecycledViewPool().setMaxRecycledViews(0, 0);

        // user input
        binding.messagesInputText.setOnClickListener(v -> {

            v.requestFocus();
            UiUtils.showKeyboard(v.getContext(), v);
        });

        binding.messagesInputText.setOnEditorActionListener((v, actionId, event) -> {

            // On DONE pressed
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onUserInput(v);
                return true;
            }

            return false;
        });

        // send button
        binding.messagesSendBtn.setOnClickListener(v -> {

            onUserInput(v);
        });
    }

    private void onUserInput(View view) {

        String userText = binding.messagesInputText.getText().toString();
        if (TextUtils.isEmpty(userText)) {
            Toast.makeText(view.getContext(), getString(R.string.error_empty_send), Toast.LENGTH_SHORT).show();
            return;
        }

        Message message = Message.builder()
                .type(MessageType.USER)
                .username(user.getUsername())
                .message(userText)
                .build();

        omiliaClient.sendMessage(dialogId, message.getMessage());
        messagesAdapter.add(message);
        binding.messagesInputText.setText("");
    }

    // chat listener
    @Override
    public void onMessage(Message data) {

        if (isFragmentAttached) {

            mActivity.runOnUiThread(() -> {

                messagesAdapter.add(data);
            });
        }
    }
}
