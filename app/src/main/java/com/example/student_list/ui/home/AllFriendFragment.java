package com.example.student_list.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.student_list.databinding.FragmentAllFriendsBinding;
import com.example.student_list.models.Student;
import com.example.student_list.room.AppDatabase;
import com.example.student_list.room.StudentDao;

import java.util.ArrayList;
import java.util.List;

public class AllFriendFragment extends Fragment {

    private FragmentAllFriendsBinding binding;

    private AppDatabase appDatabase;
    private StudentDao studentDao;
    private StudentAdapter studentAdapter;
    private List<Student> originalList = new ArrayList<>();
    private String selectedCategory = "Все"; // По умолчанию - все категории

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAllFriendsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        studentAdapter = new StudentAdapter();
        binding.rvMain.setAdapter(studentAdapter);

        appDatabase = Room.databaseBuilder(binding.getRoot().getContext(), AppDatabase.class, "database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        studentDao = appDatabase.studentDao();

        originalList = new ArrayList<>(studentDao.getAll());
        updateUI(originalList);

        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Не нужно обрабатывать отправку текста
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterContacts(newText, selectedCategory); // Вызываем метод фильтрации
                return true;
            }
        });

        // Настройка Spinner (категории)
        String[] categories = {"Все", "Семья", "Работа", "Друзья"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories);
        binding.spinnerFilter.setAdapter(adapter);

        // Обработка выбора категории
        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = categories[position];
                filterContacts(binding.searchView.getQuery().toString(), selectedCategory);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return root;
    }

    // Фильтрация списка по имени, номеру и категории
    private void filterContacts(String query, String category) {
        List<Student> filteredList = new ArrayList<>();

        for (Student student : originalList) {
            boolean matchesQuery = student.getName_surname().toLowerCase().contains(query.toLowerCase()) ||
                    student.getTel_number().contains(query);
            boolean matchesCategory = category.equals("Все") || student.getCategory().equals(category);

            if (matchesQuery && matchesCategory) {
                filteredList.add(student);
            }
        }
        updateUI(filteredList);
    }

    private void updateUI(List<Student> students) {
        if (students.isEmpty()) {
            binding.rvMain.setVisibility(View.GONE);
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            binding.rvMain.setVisibility(View.VISIBLE);
            binding.emptyStateLayout.setVisibility(View.GONE);
            studentAdapter.setList(students);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}