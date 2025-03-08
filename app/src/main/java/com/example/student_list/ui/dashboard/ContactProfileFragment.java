package com.example.student_list.ui.dashboard;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.example.student_list.R;
import com.example.student_list.databinding.FragmentContactProfileBinding;
import com.example.student_list.models.Student;
import com.example.student_list.room.AppDatabase;
import com.example.student_list.room.StudentDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ContactProfileFragment extends Fragment {
    private FragmentContactProfileBinding binding;
    private StudentDao studentDao;
    private Student student;
    private long contactId;
    private Bitmap selectedBitmap;
    private boolean isImageChanged = false;

    private ActivityResultLauncher<String> contentLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContactProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Получаем contactId из аргументов
        if (getArguments() != null) {
            contactId = getArguments().getLong("contactId", -1);
        }

        // Инициализация базы данных
        AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
        studentDao = db.studentDao();

        // Загружаем данные контакта
        loadContact();

        // Настройка загрузки фото
        contentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        try {
                            selectedBitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
                            binding.imageProfile.setImageBitmap(selectedBitmap);
                            isImageChanged = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // Настройка камеры
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                if (extras != null) {
                    selectedBitmap = (Bitmap) extras.get("data");
                    binding.imageProfile.setImageBitmap(selectedBitmap);
                    isImageChanged = true;
                }
            } else {
                Toast.makeText(requireContext(), "Фото не сделано", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка загрузки фото из галереи
        binding.btnChangePhoto.setOnClickListener(v -> contentLauncher.launch("image/*"));

        // Кнопка съемки фото камерой
        binding.btnTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                cameraLauncher.launch(takePictureIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Камера недоступна", Toast.LENGTH_SHORT).show();
            }
        });

        // Кнопка сохранения изменений
        binding.btnSaveChanges.setOnClickListener(v -> saveChanges());

        // Кнопка удаления контакта
        binding.btnDeleteContact.setOnClickListener(v -> deleteContact());
    }

    private void loadContact() {
        if (contactId == -1) {
            Toast.makeText(requireContext(), "Ошибка загрузки контакта", Toast.LENGTH_SHORT).show();
            return;
        }

        student = studentDao.getById((int) contactId); // Загружаем контакт по ID

        if (student != null) {
            binding.editName.setText(student.getName_surname());
            binding.editTelNumber.setText(student.getTel_number());
            binding.editDesc.setText(student.getDesc());

            Bitmap bitmap = BitmapFactory.decodeByteArray(student.getImage(), 0, student.getImage().length);
            binding.imageProfile.setImageBitmap(bitmap);
        }
    }

    private void saveChanges() {
        if (student == null) return;

        String newName = binding.editName.getText().toString();
        String newTel = binding.editTelNumber.getText().toString();
        String newDesc = binding.editDesc.getText().toString();

        student.setName_surname(newName);
        student.setTel_number(newTel);
        student.setDesc(newDesc);

        // Обновляем фото, если оно изменилось
        if (isImageChanged && selectedBitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            boolean success = selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            if (success) {
                student.setImage(baos.toByteArray());
            } else {
                Toast.makeText(requireContext(), "Ошибка обработки фото", Toast.LENGTH_SHORT).show();
            }
        }

        studentDao.update(student);

        Toast.makeText(requireContext(), "Контакт обновлен", Toast.LENGTH_SHORT).show();

        // Вернуться в список контактов
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.navigation_all_friends);
    }

    private void deleteContact() {
        if (student == null) return;

        studentDao.delete(student);

        Toast.makeText(requireContext(), "Контакт удален", Toast.LENGTH_SHORT).show();

        // Вернуться в список контактов
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.navigation_all_friends);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
