package com.omilia.ui.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.omilia.demo.R;
import com.omilia.demo.databinding.PlainMessageBinding;
import com.omilia.demo.databinding.PlainMessageUserBinding;
import com.omilia.sdk.models.Message;
import com.omilia.sdk.models.MessageType;
import com.omilia.sdk.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.CLIPBOARD_SERVICE;

public class MessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnCreateContextMenuListener {

    // constants
    private static final int TYPE_USER_PLAIN = 100;

    // attributes
    private final Context context;
    private List<Message> messages;

    private ClipboardManager clipboardMgr;

    // constructors
    public MessagesAdapter(Context context) {

        this.context = context;
        this.messages = new ArrayList<>();
        this.clipboardMgr = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
    }

    // public
    public List<Message> getData() {

        return this.messages;
    }

    public void setData(List<Message> data) {

        if (data == null) {
            this.messages = new ArrayList<>();
        }

        this.messages = data;
        notifyDataSetChanged();
    }

    public void add(Message message) {

        if (message == null) {
            return;
        }

        if (this.messages.isEmpty()) {
            this.messages.add(message);
            notifyItemInserted(0);
            return;
        }

        int lastIndex = this.messages.size() - 1;
        if (this.messages.get(lastIndex).getServerMessageType() == Constants.TYPE_TYPING_ON) {
            this.messages.set(lastIndex, message);
            notifyItemChanged(lastIndex);

            return;
        }

        this.messages.add(message);
        notifyItemInserted(this.messages.size());
    }

    // context menu listener
    // NOTE: setting user option to copy text on chat bubble long press
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

        menu.add(0, v.getId(), 0, context.getString(R.string.copy));
        TextView textView = (TextView) v;
        ClipData clipData = ClipData.newPlainText("text", textView.getText());
        assert clipboardMgr != null;
        clipboardMgr.setPrimaryClip(clipData);
    }

    // overrides
    @Override
    public int getItemCount() {

        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        MessageType type = message.getType();
        if (type == MessageType.USER) {
            return TYPE_USER_PLAIN;
        }

        return (type != null) ? type.ordinal() : 0;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) throws ClassCastException {

        LayoutInflater inflater = LayoutInflater.from(this.context);

        if (viewType == TYPE_USER_PLAIN) {
            PlainMessageUserBinding binding = PlainMessageUserBinding.inflate(inflater, viewGroup, false);
            return new UserPlainViewHolder(binding);
        }

        PlainMessageBinding binding = PlainMessageBinding.inflate(inflater, viewGroup, false);
        return new BotPlainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) throws ClassCastException {

        int type = getItemViewType(position);

        // handle user messages
        if (type == TYPE_USER_PLAIN) {
            handleUserPlain((UserPlainViewHolder) holder, position);
            return;
        }

        // handle bot messages
        handleBotPlain((BotPlainViewHolder) holder, position);
    }

    // private
    private void handleUserPlain(@NonNull UserPlainViewHolder holder, final int position) {

        Message message = messages.get(position);
        holder.name.setText(message.getUsername());
        holder.body.setText(message.getMessage());
    }

    private void handleBotPlain(@NonNull BotPlainViewHolder holder, final int position) {

        Message message = messages.get(position);

        if (message.getUiComponent() != null) {
            return;
        }

        holder.name.setText(message.getUsername());

        if (message.getServerMessageType() == Constants.TYPE_TYPING_ON) {
            holder.body.setVisibility(View.GONE);
            holder.typingOn.setVisibility(View.VISIBLE);

            return;
        }

        holder.body.setText(message.getMessage());
        holder.body.setVisibility(View.VISIBLE);
        holder.typingOn.setVisibility(View.GONE);
    }

    // View Holder
    public class BotPlainViewHolder extends RecyclerView.ViewHolder {

        // attributes
        TextView name;
        TextView body;
        GifImageView typingOn;

        // constructors
        private BotPlainViewHolder(PlainMessageBinding binding) {

            super(binding.getRoot());

            name = binding.name;
            body = binding.messageBody;
            typingOn = binding.messageTypingOn;

            body.setOnCreateContextMenuListener(MessagesAdapter.this);
        }
    }

    public class UserPlainViewHolder extends RecyclerView.ViewHolder {

        // attributes
        TextView name;
        TextView body;

        // constructors
        private UserPlainViewHolder(PlainMessageUserBinding binding) {

            super(binding.getRoot());

            name = binding.name;
            body = binding.messageBody;

            body.setOnCreateContextMenuListener(MessagesAdapter.this);
        }
    }
}
