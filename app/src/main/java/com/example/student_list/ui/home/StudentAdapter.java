package com.example.student_list.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.appcompat.widget.PopupMenu;


import com.example.student_list.R;
import com.example.student_list.databinding.ItemCardBinding;
import com.example.student_list.models.Student;
import com.example.student_list.room.AppDatabase;
import com.example.student_list.room.StudentDao;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    List<Student> list = new ArrayList<>();
    private StudentDao studentDao;
    Context context;
    Student newStudent;
    Boolean is_passed;

    public void setList(List<Student> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardBinding binding = ItemCardBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        studentDao = Room.databaseBuilder(
                        holder.binding.getRoot().getContext(),
                        AppDatabase.class, "database")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
                .studentDao();
        Student student = list.get(position);
        holder.binding.nameCard.setText(student.getName_surname());
        holder.binding.numberCard.setText(student.getTel_number());
        holder.binding.cardDesc.setText(student.getDesc());
        holder.binding.imageCard.setImageBitmap(BitmapFactory.decodeByteArray(
                student.getImage(), 0, student.getImage().length));
        holder.binding.dropdownMenu.setOnClickListener(v -> showPopupMenu(holder, student, position));

        holder.binding.getRoot().setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putLong("contactId", student.getId()); // Передаем ID контакта

            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.navigation_contact_profile, bundle);
        });

    }

    private void showPopupMenu(ViewHolder holder, Student student, int position) {
        Context context = holder.binding.getRoot().getContext();

        PopupMenu popup = new PopupMenu(context, holder.binding.dropdownMenu);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.call) {
                makeCall(context, student.getTel_number());
                return true;
            } else if (itemId == R.id.send_message) {
                sendMessage(context, student.getTel_number());
                return true;
            } else if (itemId == R.id.delete) {
                showDeleteDialog(holder, student, position);
                return true;
            } else {
                Toast.makeText(context, "Ничего не выбрано", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        popup.show();
    }

    private void makeCall(Context context, String phoneNumber) {
        if (context == null) {
            Toast.makeText(context, "Ошибка: контекст отсутствует", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));

        try {
            context.startActivity(callIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка при попытке вызова", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(Context context, String phoneNumber) {
        if (context == null) {
            Toast.makeText(context, "Ошибка: контекст отсутствует", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
        smsIntent.setData(Uri.parse("sms:" + phoneNumber));

        try {
            context.startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(context, "Ошибка при отправке сообщения", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteDialog(ViewHolder holder, Student student, int position) {
        new AlertDialog.Builder(holder.binding.getRoot().getContext())
                .setTitle("Удаление контакта")
                .setMessage("Вы уверены, что хотите удалить этот контакт?")
                .setPositiveButton("Удалить", (dialog, which) -> {
                    studentDao.delete(student);
                    list.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, list.size());
                    Toast.makeText(holder.binding.getRoot().getContext(), "Контакт удален!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCardBinding binding;

        public ViewHolder(@NonNull ItemCardBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }
    }
}
