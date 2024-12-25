<template>
  <div class="rich-text-editor">
    <div style="border: 1px solid #ccc">
      <Toolbar
        style="border-bottom: 1px solid #ccc"
        :editor="editorRef"
        :defaultConfig="toolbarConfig"
        mode="default"
      />
      <Editor
        style="height: 400px"
        v-model="valueHtml"
        :defaultConfig="editorConfig"
        mode="default"
        @onCreated="handleCreated"
        @onChange="handleChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, shallowRef, onBeforeUnmount, watch } from 'vue'
import '@wangeditor/editor/dist/css/style.css'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const props = defineProps({
  value: {
    type: String,
    default: ''
  },
  height: {
    type: Number,
    default: 400
  }
})

const emit = defineEmits(['update:value'])

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 内容 HTML
const valueHtml = ref('')

// 编辑器配置
const editorConfig = {
  placeholder: '请输入内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/file/upload',
      fieldName: 'file',
      maxFileSize: 2 * 1024 * 1024,
      maxNumberOfFiles: 10,
      allowedFileTypes: ['image/*'],
      metaWithUrl: true,
      customInsert: (res, insertFn) => {
        if (res.code === 200) {
          insertFn(res.data, '', '')
        }
      }
    }
  }
}

// 工具栏配置
const toolbarConfig = {
  excludeKeys: []
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

// 编辑器创建完成时的事件
const handleCreated = (editor) => {
  editorRef.value = editor
  valueHtml.value = props.value
}

// 编辑器内容变化时的事件
const handleChange = (editor) => {
  emit('update:value', editor.getHtml())
}

// 监听 value 变化
watch(
  () => props.value,
  (newValue) => {
    if (newValue !== valueHtml.value) {
      valueHtml.value = newValue
    }
  }
)
</script>

<style lang="less" scoped>
.rich-text-editor {
  :deep(.w-e-text-container) {
    height: v-bind('props.height + "px"') !important;
  }
}
</style> 