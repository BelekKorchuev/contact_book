package com.example.student_list.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.room.Room;

import com.example.student_list.databinding.FragmentAllFriendsBinding;
import com.example.student_list.models.Student;
import com.example.student_list.room.AppDatabase;
import com.example.student_list.room.StudentDao;

import java.util.List;

public class AllFriendFragment extends Fragment {

    private FragmentAllFriendsBinding binding;

    private AppDatabase appDatabase;
    private StudentDao studentDao;
    private StudentAdapter studentAdapter;

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
        List<Student> students = studentDao.getAll();

        updateUI(students);

        return root;
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