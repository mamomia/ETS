package com.umt.ameer.ets.networkmodels;

/**
 * Created by Mushi on 2/3/2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("user_info")
    @Expose
    private UserInfo userInfo;
    @SerializedName("area")
    @Expose
    private Area area;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public class Area {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("radius")
        @Expose
        private String radius;
        @SerializedName("center_point")
        @Expose
        private String centerPoint;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRadius() {
            return radius;
        }

        public void setRadius(String radius) {
            this.radius = radius;
        }

        public String getCenterPoint() {
            return centerPoint;
        }

        public void setCenterPoint(String centerPoint) {
            this.centerPoint = centerPoint;
        }

    }

    public class UserInfo {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("emp_name")
        @Expose
        private String empName;
        @SerializedName("address_id")
        @Expose
        private String addressId;
        @SerializedName("phone_no")
        @Expose
        private String phoneNo;
        @SerializedName("roles_id")
        @Expose
        private String rolesId;
        @SerializedName("employee_id")
        @Expose
        private String employeeId;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("password")
        @Expose
        private String password;
        @SerializedName("join_date")
        @Expose
        private String joinDate;
        @SerializedName("emp_dp")
        @Expose
        private String empDp;
        @SerializedName("emp_status")
        @Expose
        private String empStatus;
        @SerializedName("break_content")
        @Expose
        private String breakContent;
        @SerializedName("area_id")
        @Expose
        private String areaId;
        @SerializedName("emp_lat")
        @Expose
        private String empLat;
        @SerializedName("emp_long")
        @Expose
        private String empLong;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmpName() {
            return empName;
        }

        public void setEmpName(String empName) {
            this.empName = empName;
        }

        public String getAddressId() {
            return addressId;
        }

        public void setAddressId(String addressId) {
            this.addressId = addressId;
        }

        public String getPhoneNo() {
            return phoneNo;
        }

        public void setPhoneNo(String phoneNo) {
            this.phoneNo = phoneNo;
        }

        public String getRolesId() {
            return rolesId;
        }

        public void setRolesId(String rolesId) {
            this.rolesId = rolesId;
        }

        public String getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(String employeeId) {
            this.employeeId = employeeId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }

        public String getEmpDp() {
            return empDp;
        }

        public void setEmpDp(String empDp) {
            this.empDp = empDp;
        }

        public String getEmpStatus() {
            return empStatus;
        }

        public void setEmpStatus(String empStatus) {
            this.empStatus = empStatus;
        }

        public String getBreakContent() {
            return breakContent;
        }

        public void setBreakContent(String breakContent) {
            this.breakContent = breakContent;
        }

        public String getAreaId() {
            return areaId;
        }

        public void setAreaId(String areaId) {
            this.areaId = areaId;
        }

        public String getEmpLat() {
            return empLat;
        }

        public void setEmpLat(String empLat) {
            this.empLat = empLat;
        }

        public String getEmpLong() {
            return empLong;
        }

        public void setEmpLong(String empLong) {
            this.empLong = empLong;
        }

    }

}
