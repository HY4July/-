<script>
import { getDiscussionsPage, replyDiscussion, deleteDiscussion } from '@/api/discussion';
import { getCourseById } from '@/api/course';

export default {
  name: 'CourseDiscussionsManager',
  props: ['courseId'],
  data() {
    return {
      courseName: '',
      discussions: [],
      pageNum: 1,
      pageSize: 10,
      totalDiscussions: 0,
      // ... 其他 data 属性
      currentUser: null,
      defaultAvatar: require('@/assets/imgs/default-avatar.png'),
    };
  },
  created() {
    this.currentUser = JSON.parse(localStorage.getItem('xm-user') || '{}');
    console.log('CourseDiscussionsManager created. Course ID from props:', this.courseId); // 调试
    this.fetchCourseName();
    this.loadDiscussions();
  },
  methods: {
    async fetchCourseName() {
      console.log('Fetching course name for courseId:', this.courseId); // 调试
      try {
        const res = await getCourseById(this.courseId);
        console.log('fetchCourseName response:', res); // 调试
        if (res.code === '200' && res.data) {
          this.courseName = res.data.name;
        } else {
          this.$message.error(`获取课程名称失败: ${res.msg || '未知错误'}`);
          this.courseName = `课程 #${this.courseId}`;
        }
      } catch (error) {
        this.$message.error("获取课程名称异常");
        console.error("获取课程名称异常:", error);
        this.courseName = `课程 #${this.courseId}`;
      }
    },
    async loadDiscussions() {
      console.log(`Loading discussions for courseId: ${this.courseId}, pageNum: ${this.pageNum}, pageSize: ${this.pageSize}`); // 调试
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          courseId: parseInt(this.courseId), // 确保 courseId 是数字类型
        };
        const res = await getDiscussionsPage(params);
        console.log('loadDiscussions response:', res); // 调试
        if (res.code === '200' && res.data) {
          this.discussions = res.data.list || [];
          this.totalDiscussions = res.data.total || 0;
          if (this.discussions.length === 0) {
            console.log('No discussions found for this course.'); // 调试
          }
        } else {
          this.$message.error(res.msg || '加载讨论列表失败');
          this.discussions = []; // 清空数据以避免显示旧数据
          this.totalDiscussions = 0;
        }
      } catch (error) {
        this.$message.error('加载讨论列表异常');
        console.error("加载讨论列表异常:", error);
        this.discussions = [];
        this.totalDiscussions = 0;
      }
    },
    // ... 其他 methods
  },
};
</script>