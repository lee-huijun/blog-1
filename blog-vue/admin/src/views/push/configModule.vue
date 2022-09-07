<template>
  <div>
    <el-dialog v-bind="$attrs" v-on="$listeners" @open="onOpen" @close="onClose" title="Dialog Title">
      <el-row :gutter="15">
        <el-form ref="elForm" :model="formData" :rules="rules" size="medium" label-width="100px">
          <el-col :span="12">
            <el-row gutter="15">
              <el-col :span="24">
                <el-form-item label="AppID" prop="field105">
                  <el-input v-model="formData.field105" placeholder="请输入AppID" clearable
                    :style="{width: '100%'}"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="公众号秘钥" prop="field104">
                  <el-input v-model="formData.field104" placeholder="请输入公众号秘钥" clearable
                    :style="{width: '100%'}"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="全局模板ID" prop="field103">
                  <el-input v-model="formData.field103" placeholder="请输入全局模板ID" clearable
                    :style="{width: '100%'}"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="推送时间" prop="field107">
                  <el-input v-model="formData.field107" placeholder="请输入推送时间" clearable
                    :style="{width: '100%'}"></el-input>
                </el-form-item>
              </el-col>
            </el-row>
          </el-col>
        </el-form>
      </el-row>
      <div slot="footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
export default {
  inheritAttrs: false,
  components: {},
  props: [],
  data() {
    return {
      formData: {
        field105: undefined,
        field104: undefined,
        field103: undefined,
        field107: undefined,
      },
      rules: {
        field105: [{
          required: true,
          message: '请输入AppID',
          trigger: 'blur'
        }],
        field104: [{
          required: true,
          message: '请输入公众号秘钥',
          trigger: 'blur'
        }],
        field103: [{
          required: true,
          message: '请输入全局模板ID',
          trigger: 'blur'
        }],
        field107: [{
          required: true,
          message: '请输入推送时间',
          trigger: 'blur'
        }],
      },
    }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    onOpen() {},
    onClose() {
      this.$refs['elForm'].resetFields()
    },
    close() {
      this.$emit('update:visible', false)
    },
    handleConfirm() {
      this.$refs['elForm'].validate(valid => {
        if (!valid) return
        this.close()
      })
    },
  }
}

</script>
<style>
</style>
