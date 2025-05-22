SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
                          `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                          `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
                          `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
                          `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
                          `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
                          `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色标识',
                          `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
                          `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                          PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456', '管理员', 'http://localhost:9090/files/1697438073596-avatar.png', 'ADMIN', '13677889922', 'admin@xm.com');
-- ----------------------------
-- Table structure for teacher
-- ----------------------------
DROP TABLE IF EXISTS `teacher`;
CREATE TABLE `teacher`  (
                            `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
                            `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
                            `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
                            `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
                            `phone` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
                            `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
                            `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '职称',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教师信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teacher
-- ----------------------------
INSERT INTO `teacher` VALUES (1, 'teacher', '123456', '教师', 'http://localhost:9090/files/1747754945463-1697438073596-avatar.png', 'TEACHER', NULL, NULL, NULL);
-- ----------------------------
-- Table structure for student
-- ----------------------------
DROP TABLE IF EXISTS `student`;
CREATE TABLE `student`  (
                            `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                            `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '用户名',
                            `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
                            `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像',
                            `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
                            `college_id` int(0) NULL DEFAULT NULL COMMENT '学院ID',
                            `speciality_id` int(0) NULL DEFAULT NULL COMMENT '专业ID',
                            `class_id` int(0) NULL DEFAULT NULL COMMENT '班级ID',
                            `score` int(0) NULL DEFAULT 0 COMMENT '学分',
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student
-- ----------------------------
INSERT INTO `student` VALUES (1, 'student', '123456', '学生', 'http://localhost:9090/files/1747754993438-1697438073596-avatar.png', 'STUDENT', NULL, NULL, NULL, 0);

-- ----------------------------
-- Table structure for notice
-- ----------------------------
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice`  (
                           `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                           `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
                           `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内容',
                           `time` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建时间',
                           `user` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
                           PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '公告信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notice
-- ----------------------------
INSERT INTO `notice` VALUES (1, '今天系统正式上线，开始内测', '今天系统正式上线，开始内测', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (2, '所有功能都已完成，可以正常使用', '所有功能都已完成，可以正常使用', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (3, '日记', '今天天气很不错，可以出去一起玩了', '2023-09-05', 'admin');
INSERT INTO `notice` VALUES (4, '这是一条教师公告', 'test1', '2025-05-22', 'teacher');
INSERT INTO `notice` VALUES (7, '1111', 'gwrG', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (8, '222', 'Fwgf\n', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (9, '3333', 'jxdtyu', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (10, '4444', 'g围绕', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (11, '5555', 'JDYK', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (12, '666', 'BFAE地方很多', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (13, '777', '爱他人工复核几个', '2025-05-22', 'admin');
INSERT INTO `notice` VALUES (14, '888', 'nest对于', '2025-05-22', 'admin');

-- ----------------------------
-- Table structure for classes
-- ----------------------------
DROP TABLE IF EXISTS `classes`;
CREATE TABLE `classes`  (
                            `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '班级ID',
                            `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '班级名称',
                            `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '班级描述',
                            `teacher_id` int(0) NULL DEFAULT NULL COMMENT '负责教师ID/班主任ID',
                            `speciality_id` int(0) NULL DEFAULT NULL COMMENT '专业ID',
                            PRIMARY KEY (`id`) USING BTREE,
                            INDEX `fk_class_teacher`(`teacher_id`) USING BTREE,
                            CONSTRAINT `fk_class_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '班级信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of classes
-- ----------------------------
INSERT INTO `classes` VALUES (1, '物联网214', '2021级', 1, 1111);

-- ----------------------------
-- Table structure for course
-- ----------------------------
DROP TABLE IF EXISTS `course`;
CREATE TABLE `course`  (
                           `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '课程ID',
                           `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '课程名称',
                           `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程类型',
                           `teacher_id` int(0) NULL DEFAULT NULL COMMENT '授课教师ID',
                           `score` int(0) NULL DEFAULT NULL COMMENT '课程学分',
                           `num` int(0) NULL DEFAULT NULL COMMENT '人数限制',
                           `room` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上课教室',
                           `week` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '星期几上课',
                           `segment` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程节数/时段',
                           `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '课程状态',
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `fk_course_teacher`(`teacher_id`) USING BTREE,
                           CONSTRAINT `fk_course_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '课程信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of course
-- ----------------------------
INSERT INTO `course` VALUES (1, '高等数学', '考试课', 1, 5, 120, '401', '周一、周四', '上午1-2节', '进行中');

-- ----------------------------
-- Table structure for assignment_info
-- ----------------------------
DROP TABLE IF EXISTS `assignment_info`;
CREATE TABLE `assignment_info`  (
                                    `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '作业ID',
                                    `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作业标题',
                                    `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '作业内容/说明',
                                    `course_id` int(0) NOT NULL COMMENT '关联课程ID',
                                    `teacher_id` int(0) NOT NULL COMMENT '发布教师ID',
                                    `publish_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发布时间',
                                    `due_time` datetime(0) NULL DEFAULT NULL COMMENT '截止时间',
                                    `attachment_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作业附件路径 (可选)',
                                    PRIMARY KEY (`id`) USING BTREE,
                                    INDEX `fk_assignment_course`(`course_id`) USING BTREE,
                                    INDEX `fk_assignment_teacher`(`teacher_id`) USING BTREE,
                                    CONSTRAINT `fk_assignment_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `fk_assignment_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '作业信息表 (教师发布)' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of assignment_info
-- ----------------------------
INSERT INTO `assignment_info` VALUES (1, '微积分作业一', '大师GQEG傅觉OI看AJSY\nVOLASDOPMG', 1, 1, '2025-05-22 17:36:18', '2025-06-30 00:00:00', 'http://localhost:9090/files/1747906576843-作业test.txt');


-- ----------------------------
-- Table structure for student_course_enrollment
-- ----------------------------
DROP TABLE IF EXISTS `student_course_enrollment`;
CREATE TABLE `student_course_enrollment`  (
                                              `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '选课记录ID',
                                              `student_id` int(0) NOT NULL COMMENT '学生ID',
                                              `course_id` int(0) NOT NULL COMMENT '课程ID',
                                              `enrollment_date` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '选课时间',
                                              `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'ENROLLED' COMMENT '选课状态 (ENROLLED, DROPPED, COMPLETED)',
                                              PRIMARY KEY (`id`) USING BTREE,
                                              UNIQUE INDEX `uk_student_course`(`student_id`, `course_id`) USING BTREE,
                                              INDEX `fk_enroll_course`(`course_id`) USING BTREE,
                                              CONSTRAINT `fk_enroll_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                              CONSTRAINT `fk_enroll_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生选课表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_course_enrollment
-- ----------------------------

-- ----------------------------
-- Table structure for teaching_resource
-- ----------------------------
DROP TABLE IF EXISTS `teaching_resource`;
CREATE TABLE `teaching_resource`  (
                                      `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '资源ID',
                                      `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '资源名称',
                                      `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件类型（如: pdf, mp4, docx）',
                                      `path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储路径/URL',
                                      `teacher_id` int(0) NOT NULL COMMENT '上传教师ID',
                                      `course_id` int(0) NULL DEFAULT NULL COMMENT '关联课程ID',
                                      `upload_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '上传时间',
                                      `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '资源描述 (可选)',
                                      `size` bigint(0) NULL DEFAULT 0 COMMENT '文件大小 (字节)',
                                      `downloads` int(0) NULL DEFAULT 0 COMMENT '下载次数',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `fk_resource_teacher`(`teacher_id`) USING BTREE,
                                      INDEX `fk_resource_course`(`course_id`) USING BTREE,
                                      CONSTRAINT `fk_resource_course` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
                                      CONSTRAINT `fk_resource_teacher` FOREIGN KEY (`teacher_id`) REFERENCES `teacher` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '教学资源信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of teaching_resource
-- ----------------------------
INSERT INTO `teaching_resource` VALUES (1, '示例1', 'txt', 'http://localhost:9090/files/1747900784884-ai随笔.txt', 1, 1, '2025-05-22 15:59:48', NULL, 0, 1);

-- ----------------------------
-- Table structure for student_assignment
-- ----------------------------
DROP TABLE IF EXISTS `student_assignment`;
CREATE TABLE `student_assignment`  (
                                       `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '提交记录ID',
                                       `assignment_id` int(0) NOT NULL COMMENT '关联的作业ID (来自assignment_info)',
                                       `student_id` int(0) NOT NULL COMMENT '提交学生ID',
                                       `submission_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '学生提交的文本内容 (可选)',
                                       `submission_file_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '学生提交的文件路径',
                                       `submission_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '提交时间',
                                       `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'PENDING_REVIEW' COMMENT '作业状态 (PENDING_REVIEW, REVIEWED, LATE)',
                                       `score` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '作业得分/等级',
                                       `teacher_comment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '教师评语',
                                       `graded_time` datetime(0) NULL DEFAULT NULL COMMENT '批改时间',
                                       `graded_file_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '教师批改后上传的文件路径 (可选)',
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `uk_student_assignment`(`assignment_id`, `student_id`) USING BTREE,
                                       INDEX `fk_studentassign_student`(`student_id`) USING BTREE,
                                       CONSTRAINT `fk_studentassign_assignmentinfo` FOREIGN KEY (`assignment_id`) REFERENCES `assignment_info` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                                       CONSTRAINT `fk_studentassign_student` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '学生作业提交表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of student_assignment
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
