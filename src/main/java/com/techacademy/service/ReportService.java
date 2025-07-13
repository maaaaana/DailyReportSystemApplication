package com.techacademy.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.techacademy.constants.ErrorKinds;
import com.techacademy.entity.Employee;
import com.techacademy.repository.EmployeeRepository;

@Service
public class ReportService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public ReportService(EmployeeRepository employeeRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 従業員保存（新規作成用）
    @Transactional
    public ErrorKinds save(Employee employee) {
        // パスワードチェック
        ErrorKinds result = employeePasswordCheck(employee);
        if (ErrorKinds.CHECK_OK != result) {
            return result;
        }

        // 従業員番号重複チェック
        if (findByCode(employee.getCode()) != null) {
            return ErrorKinds.DUPLICATE_ERROR;
        }

        employee.setDeleteFlg(false);
        LocalDateTime now = LocalDateTime.now();
        employee.setCreatedAt(now);
        employee.setUpdatedAt(now);

        // ハッシュ化（2重防止つき）
        encodePasswordIfNeeded(employee);

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }

    // 従業員削除
    @Transactional
    public ErrorKinds delete(String code, UserDetail userDetail) {
        if (code.equals(userDetail.getEmployee().getCode())) {
            return ErrorKinds.LOGINCHECK_ERROR;
        }

        Employee employee = findByCode(code);
        LocalDateTime now = LocalDateTime.now();
        employee.setUpdatedAt(now);
        employee.setDeleteFlg(true);

        return ErrorKinds.SUCCESS;
    }

    // 一覧取得
    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    // 1件取得
    public Employee findByCode(String code) {
        Optional<Employee> option = employeeRepository.findById(code);
        return option.orElse(null);
    }

    // パスワードバリデーション
    private ErrorKinds employeePasswordCheck(Employee employee) {
        if (isHalfSizeCheckError(employee)) {
            return ErrorKinds.HALFSIZE_ERROR;
        }

        if (isOutOfRangePassword(employee)) {
            return ErrorKinds.RANGECHECK_ERROR;
        }

        return ErrorKinds.CHECK_OK;
    }

    // 半角英数字チェック
    private boolean isHalfSizeCheckError(Employee employee) {
        Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");
        Matcher matcher = pattern.matcher(employee.getPassword());
        return !matcher.matches();
    }

    // 8～16文字チェック
    public boolean isOutOfRangePassword(Employee employee) {
        int passwordLength = employee.getPassword().length();
        return passwordLength < 8 || 16 < passwordLength;
    }

    // ✅ 追加：パスワードがハッシュ済みかを判断してハッシュ化（重ねがけ防止）
    private void encodePasswordIfNeeded(Employee employee) {
        if (employee.getPassword() != null && !employee.getPassword().startsWith("$2a$")) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }
    }

    // ✅ 更新・新規共通の保存処理
    @Transactional
    public ErrorKinds save(Employee employee, boolean isNew) {
        if (isNew) {
            if (findByCode(employee.getCode()) != null) {
                return ErrorKinds.DUPLICATE_ERROR;
            }
            employee.setCreatedAt(LocalDateTime.now());
        } else {
            Employee dbEmployee = findByCode(employee.getCode());
            if (dbEmployee == null) {
                return ErrorKinds.NOTFOUND_ERROR;
            }
            employee.setCreatedAt(dbEmployee.getCreatedAt());
        }

        employee.setUpdatedAt(LocalDateTime.now());
        employee.setDeleteFlg(false);

        if (employee.getPassword() == null || "".equals(employee.getPassword())) {
            if (!isNew) {
                employee.setPassword(findByCode(employee.getCode()).getPassword());
            } else {
                return ErrorKinds.BLANK_ERROR;
            }
        } else {
            ErrorKinds result = employeePasswordCheck(employee);
            if (ErrorKinds.CHECK_OK != result) {
                return result;
            }

            // ✅ ハッシュ化（すでにされてるかも確認）
            encodePasswordIfNeeded(employee);
        }

        employeeRepository.save(employee);
        return ErrorKinds.SUCCESS;
    }
}
