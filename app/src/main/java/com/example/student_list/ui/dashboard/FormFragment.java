package com.example.student_list.ui.dashboard;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.room.Room;

import com.example.student_list.R;
import com.example.student_list.databinding.FragmentFormBinding;
import com.example.student_list.models.Student;
import com.example.student_list.room.AppDatabase;
import com.example.student_list.room.StudentDao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FormFragment extends Fragment {
    private FragmentFormBinding binding;
    private AppDatabase appDatabase;
    private StudentDao studentDao;
    private ActivityResultLauncher<String> content_l;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private Bitmap bitmapImageStudent;
    private boolean isImgSelected = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFormBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Инициализация базы данных (чтобы не создавать ее каждый раз при нажатии кнопки)
        appDatabase = Room.databaseBuilder(requireContext(), AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        studentDao = appDatabase.studentDao();

        // Заполняем Spinner (категории)
        String[] categories = {"Семья", "Работа", "Друзья"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerCategory.setAdapter(adapter);

        binding.btnLoadPhoto.setOnClickListener(v -> content_l.launch("image/*"));

        content_l = registerForActivityResult(new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri == null) {
                    Toast.makeText(requireContext(), "Выбор фото отменен", Toast.LENGTH_SHORT).show();
                    isImgSelected = false;
                    return;
                }
                try {
                    bitmapImageStudent = MediaStore.Images.Media.getBitmap(
                            requireContext().getContentResolver(), uri);
                    binding.imagePlace.setImageBitmap(bitmapImageStudent);
                    isImgSelected = true;
                } catch (IOException error) {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Ошибка загрузки фото", Toast.LENGTH_SHORT).show();
                    isImgSelected = false;
                }
            }
        );

        binding.btnTakePhoto.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                Bundle extras = result.getData().getExtras();
                if (extras != null && extras.get("data") != null) {
                    bitmapImageStudent = (Bitmap) extras.get("data");
                    binding.imagePlace.setImageBitmap(bitmapImageStudent);
                    isImgSelected = true;
                } else {
                    Toast.makeText(requireContext(), "Ошибка получения фото", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Фото не сделано", Toast.LENGTH_SHORT).show();
                isImgSelected = false;
            }
        });

        // Запрашиваем разрешение на камеру
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), "Доступ к камере запрещен", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            cameraLauncher.launch(takePictureIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "Камера недоступна", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnSave.setOnClickListener(v2 -> {
            String nameSurnameStudent = binding.editName.getText().toString();
            String telNumber = binding.editTelNumber.getText().toString();
            String desc = binding.editDesc.getText().toString();
            String category = binding.spinnerCategory.getSelectedItem().toString();

            if (desc.isEmpty()) {
                desc = "Нет описания";
            }

            // Делаем фото не обязательным, если не выбрано — используем стандартную иконку
            byte[] imageStudent = null;
            if (isImgSelected && bitmapImageStudent != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                boolean success = bitmapImageStudent.compress(Bitmap.CompressFormat.PNG, 100, baos);
                if (success) {
                    imageStudent = baos.toByteArray();
                }
            } else {
                // Загружаем стандартную иконку
                Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.profile_icon);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                defaultImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                imageStudent = baos.toByteArray();
            }

            if (nameSurnameStudent.isEmpty() || telNumber.isEmpty()) {
                Toast.makeText(requireActivity(), "Имя и телефон обязательны!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Создаем объект студента и сохраняем в базу данных
            Student student = new Student(nameSurnameStudent, telNumber, desc, category, imageStudent);
            studentDao.insert(student);

            Toast.makeText(requireActivity(), "Контакт добавлен!", Toast.LENGTH_SHORT).show();

            // Переход обратно в список контактов (AllFriendFragment)
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_all_friends);
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
