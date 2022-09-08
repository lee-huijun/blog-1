<template>
  <el-card class="main-card">
    <div class="title">{{ this.$route.name }}</div>
    <!-- 表格操作 -->
    <div class="operation-container">
      <el-button
          type="primary"
          size="small"
          icon="el-icon-plus"
          @click="openMenuModel(null)"
      >
        新增
      </el-button>
      <el-button
          type="danger"
          size="small"
          icon="el-icon-delete"
          :disabled="this.roleIdList.length == 0"
          @click="isDelete = true"
      >
        批量删除
      </el-button>
      <!-- 条件筛选 -->
      <div style="margin-left:auto">
        <el-input
            v-model="keywords"
            prefix-icon="el-icon-search"
            size="small"
            placeholder="请输入关键字"
            style="width:200px"
            @keyup.enter.native="searchRoles"
        />
        <el-button
            type="primary"
            size="small"
            icon="el-icon-search"
            style="margin-left:1rem"
            @click="searchRoles"
        >
          搜索
        </el-button>
      </div>
    </div>
    <!-- 表格展示 -->
    <el-table
        border
        :data="roleList"
        @selection-change="selectionChange"
        v-loading="loading"
    >
      <!-- 表格列 -->
      <el-table-column type="selection" width="55" />
      <el-table-column prop="name" label="好友名" align="center" />
      <el-table-column prop="wxId" label="微信号" align="center"/>
      <el-table-column
          prop="createTime"
          label="创建时间"
          width="150"
          align="center"
      >
        <template slot-scope="scope">
          <i class="el-icon-time" style="margin-right:5px" />
          {{ scope.row.createTime | date }}
        </template>
      </el-table-column>
      <!-- 列操作 -->
      <el-table-column label="操作" align="center" width="220">
        <template slot-scope="scope">
          <el-button type="text" size="mini" @click="openMenuModel(scope.row)">
            <i class="el-icon-edit" /> 修改
          </el-button>
          <el-popconfirm
              title="确定删除吗？"
              style="margin-left:10px"
              @confirm="deleteRoles(scope.row.id)"
          >
            <el-button size="mini" type="text" slot="reference">
              <i class="el-icon-delete" /> 删除
            </el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 分页 这里我后台没有传总数，但是我看SQL好像有一个去查了-->
    <el-pagination
        class="pagination-container"
        background
        @size-change="sizeChange"
        @current-change="currentChange"
        :current-page="current"
        :page-size="size"
        :total="count"
        :page-sizes="[10, 20]"
        layout="total, sizes, prev, pager, next, jumper"
    />
    <!-- 菜单对话框 -->
    <el-dialog :visible.sync="roleMenu" width="30%">
      <div class="dialog-title-container" slot="title" ref="roleTitle" />
      <el-form label-width="80px" size="medium" :model="roleForm">

        <el-form-item label="好友名字">
          <el-input v-model="roleForm.name" style="width:250px" />
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="roleForm.age" style="width:250px" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="roleForm.province" style="width:250px" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="roleForm.city" style="width:250px" />
        </el-form-item>
        <el-form-item label="微信号">
          <el-input v-model="roleForm.wxId" style="width:250px" />
        </el-form-item>
        <el-form-item label="生日">
          <el-input v-model="roleForm.birthday" style="width:250px" />
        </el-form-item>
        <el-form-item label="相爱时间">
          <el-input v-model="roleForm.loveTime" style="width:250px" />
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="roleForm.sex" style="width:250px" />
        </el-form-item>
        <el-form-item label="推送模板Id">
          <el-input v-model="roleForm.templateId" style="width:250px" />
        </el-form-item>

      </el-form>
      <div slot="footer">
        <el-button @click="roleMenu = false">取 消</el-button>
        <el-button type="primary" @click="saveOrUpdateRoleMenu">
          确 定
        </el-button>
      </div>
    </el-dialog>

    <!-- 批量删除对话框 -->
    <el-dialog :visible.sync="isDelete" width="30%">
      <div class="dialog-title-container" slot="title">
        <i class="el-icon-warning" style="color:#ff9900" />提示
      </div>
      <div style="font-size:1rem">是否删除选中项？</div>
      <div slot="footer">
        <el-button @click="isDelete = false">取 消</el-button>
        <el-button type="primary" @click="deleteRoles(null)">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </el-card>
</template>

<script>
export default {
  created() {
    this.listRoles();
  },
  data: function() {
    return {
      loading: true,
      isDelete: false,
      roleList: [],
      roleIdList: [],
      keywords: null,
      current: 1,
      size: 10,
      count: 0,
      roleMenu: false,
      roleResource: false,
      resourceList: [],
      menuList: [],
      roleForm: {
        name: "",
        age: "",
        province:"",
        city:"",
        wxId:"",
        birthday:"",
        loveTime:"",
        sex:"",
        templateId:""
      }
    };
  },
  methods: {
    searchRoles() {
      this.current = 1;
      this.listRoles();
    },
    sizeChange(size) {
      this.size = size;
      this.listRoles();
    },
    currentChange(current) {
      this.current = current;
      this.listRoles();
    },
    selectionChange(roleList) {
      this.roleIdList = [];
      roleList.forEach(item => {
        this.roleIdList.push(item.id);
      });
    },
    listRoles() {
      this.axios
          .get("/api/admin/receiver", {
            params: {
              current: this.current,
              size: this.size,
              keywords: this.keywords
            }
          })
          .then(({ data }) => {
            this.roleList = data.data;
            this.count = data.data.count;
            this.loading = false;
          });
      /*这里先不删除，防止上面用到了数据，好像是可以删除的*/
      /*this.axios.get("/api/admin/role/resources").then(({ data }) => {
        this.resourceList = data.data;
      });
      this.axios.get("/api/admin/role/menus").then(({ data }) => {
        this.menuList = data.data;
      });*/
    },
    deleteRoles(id) {
      var param = {};
      if (id == null) {
        param = { data: this.roleIdList };
      } else {
        param = { data: [id] };
      }
      this.axios.delete(`/api/admin/receiver/${id}`).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.isDelete = false;
      });
    },
    openMenuModel(role) {
      this.$nextTick(function() {
        this.$refs.menuTree.setCheckedKeys([]);
      });
      this.$refs.roleTitle.innerHTML = role ? "修改信息" : "新增角色";
      if (role != null) {
        this.roleForm = JSON.parse(JSON.stringify(role));
      } else {
        this.roleForm = {
          name: "",
          age: "",
          province:"",
          city:"",
          wxId:"",
          birthday:"",
          loveTime:"",
          templateId: "",
          sex:""
        };
      }
      this.roleMenu = true;
    },
    saveOrUpdateRoleMenu() {
      this.axios.post("/api/admin/receiver", this.roleForm).then(({ data }) => {
        if (data.flag) {
          this.$notify.success({
            title: "成功",
            message: data.message
          });
          this.listRoles();
        } else {
          this.$notify.error({
            title: "失败",
            message: data.message
          });
        }
        this.roleMenu = false;
      });
    }
  }
};
</script>
