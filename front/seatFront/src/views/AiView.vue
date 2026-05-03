<template>
  <div class="ai-chat-container" :class="theme">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ open: sidebarOpen }">
      <div class="sidebar-header">
        <button class="new-chat-btn" @click="startNewChat">
          <span class="icon">+</span>
          新建对话
        </button>
      </div>

      <div class="chat-history">
        <div
          v-for="(c,index) in chats"
          :key="c.id"
          class="history-item"
          :class="{ active: index === activeChatIndex }"
          @click="switchChat(index)"
        >
          <span class="chat-title">{{ c.title }}</span>
          <span class="chat-time">{{ c.timeLabel }}</span>
        </div>
      </div>

      <div class="sidebar-footer">
        <div class="user-profile">
          <div class="avatar">U</div>
          <span class="username">用户</span>
        </div>
      </div>
    </div>

    <!-- 主聊天区域 -->
    <div class="main-chat">
      <!-- 顶部标题栏 -->
      <div class="chat-header">
        <div class="header-left">
          <button class="sidebar-toggle" @click="toggleSidebar">☰</button>
          <h1 class="chat-title">ChatGPT</h1>
        </div>
        <div class="header-right">
          <button class="header-btn" @click="toggleTheme" :title="theme==='light'?'切换深色':'切换浅色'">
            {{ theme === 'light' ? '🌙' : '☀' }}
          </button>
          <button class="header-btn" @click="startNewChat" title="新建对话">＋</button>
          <button class="header-btn" @click="clearMessages" title="清空当前">🗑</button>
        </div>
      </div>

      <!-- 消息区域 -->
      <div class="messages-container" ref="messagesContainer">
        <!-- 欢迎界面 -->
        <div
          v-if="currentMessages.length === 0 && !loading"
          class="welcome-screen"
        >
          <div class="welcome-content">
            <div class="ai-logo">
              <div class="logo-circle">
                <span>AI</span>
              </div>
            </div>
            <h2>您好！我是您的AI助手</h2>
            <p>我可以帮助您解答问题、编写代码、创作内容等</p>

            <div class="example-prompts">
              <div
                v-for="p in examplePrompts"
                :key="p.title"
                class="prompt-card"
                @click="usePrompt(p.content)"
              >
                <div class="prompt-icon">{{ p.icon }}</div>
                <div class="prompt-text">
                  <h4>{{ p.title }}</h4>
                  <p>{{ p.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <div
          v-else
          class="messages-list"
        >
          <div
            v-for="(m,idx) in currentMessages"
            :key="idx"
            class="message"
            :class="m.role==='user'?'user-message':'ai-message'"
          >
            <div class="message-content">
              <div
                class="message-avatar"
                :class="m.role==='user'?'user-avatar':'ai-avatar'"
              >
                {{ m.role==='user'?'U':'AI' }}
              </div>
              <div
                class="message-bubble"
                :class="m.role==='user'?'user-bubble':'ai-bubble'"
              >
                <div v-if="m.type==='text'" v-html="formatContent(m.content)"></div>
                <div v-else-if="m.type==='typing'" class="typing-indicator">
                  <span></span><span></span><span></span>
                </div>
                <span class="message-time">{{ m.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <div class="input-container">
          <div
            class="input-wrapper"
            :class="{ disabled: loading }"
          >
            <textarea
              ref="textarea"
              class="message-input"
              placeholder="向ChatGPT发送消息..."
              v-model.trim="draft"
              rows="1"
              @keydown.enter.prevent="handleEnter"
              @input="autoResize"
            ></textarea>
            <button
              class="send-button"
              :disabled="!draft || loading"
              @click="send"
              title="发送 (Enter)"
            >
              <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                <path d="M2 2l12 6-12 6V2z"/>
              </svg>
            </button>
          </div>
          <div class="input-footer">
            <small>ChatGPT 可能会产生不准确的信息 · Enter 发送 / Shift+Enter 换行</small>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AiView',
  data() {
    return {
      theme: 'light',
      sidebarOpen: true,
      draft: '',
      loading: false,
      chats: [
        {
          id: Date.now(),
            title: '新的对话',
            timeLabel: '刚刚',
            messages: []
        }
      ],
      activeChatIndex: 0,
      examplePrompts: [
        { icon: '💡', title: '创意写作', content: '帮我写一个有趣的故事' },
        { icon: '🔧', title: '编程帮助', content: '解释这段代码的功能' },
        { icon: '📚', title: '学习辅导', content: '制定一个系统的学习计划' },
        { icon: '🎯', title: '技术问题', content: '说明 Vue 组件生命周期要点' },
      ],
    }
  },
  computed: {
    currentMessages() {
      return this.chats[this.activeChatIndex].messages
    }
  },
  methods: {
    toggleSidebar() {
      this.sidebarOpen = !this.sidebarOpen
    },
    toggleTheme() {
      this.theme = this.theme === 'light' ? 'dark' : 'light'
    },
    startNewChat() {
      this.chats.unshift({
        id: Date.now(),
        title: '新的对话',
        timeLabel: '刚刚',
        messages: []
      })
      this.activeChatIndex = 0
    },
    switchChat(i) {
      this.activeChatIndex = i
    },
    usePrompt(text) {
      this.draft = text
      this.$nextTick(() => {
        this.autoResize()
        this.send()
      })
    },
    handleEnter(e) {
      if (e.shiftKey) {
        // 换行
        const el = e.target
        // 光标换行后再调整高度
        this.$nextTick(this.autoResize)
        return
      }
      this.send()
    },
    send() {
      if (!this.draft || this.loading) return
      const now = new Date()
      const time = now.toTimeString().slice(0,5)
      // 推入用户消息
      this.currentMessages.push({
        role: 'user',
        type: 'text',
        content: this.escapeHtml(this.draft),
        time
      })
      // 更新标题
      if (this.chats[this.activeChatIndex].title === '新的对话') {
        this.chats[this.activeChatIndex].title = this.draft.slice(0,18)
      }
      this.draft = ''
      this.autoResize()
      this.scrollBottom()

      // 模拟AI回复
      this.loading = true
      const typingMsg = {
        role: 'assistant',
        type: 'typing',
        content: '',
        time
      }
      this.currentMessages.push(typingMsg)
      this.scrollBottom()

      setTimeout(() => {
        // 替换为真实请求逻辑
        const reply = '这是一个模拟回复：\n\n1. 你可以接入后端接口\n2. 实现流式输出\n3. 添加 Markdown 渲染'
        typingMsg.type = 'text'
        typingMsg.content = this.escapeHtml(reply).replace(/\n/g,'<br/>')
        typingMsg.time = new Date().toTimeString().slice(0,5)
        this.loading = false
        this.scrollBottom()
      }, 1200)
    },
    clearMessages() {
      this.chats[this.activeChatIndex].messages = []
    },
    autoResize() {
      const ta = this.$refs.textarea
      if (!ta) return
      ta.style.height = '24px'
      ta.style.height = Math.min(ta.scrollHeight, 120) + 'px'
    },
    scrollBottom() {
      this.$nextTick(() => {
        const box = this.$refs.messagesContainer
        if (box) box.scrollTop = box.scrollHeight
      })
    },
    formatContent(html) {
      return html
    },
    escapeHtml(str) {
      return str
        .replace(/&/g,'&amp;')
        .replace(/</g,'&lt;')
        .replace(/>/g,'&gt;')
        .replace(/"/g,'&quot;')
        .replace(/'/g,'&#39;')
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.ai-chat-container {
  position: fixed;
  inset: 0;
  display: flex;
  font-family: -apple-system,BlinkMacSystemFont,'Segoe UI','PingFang SC',sans-serif;
  background: var(--bg);
  color: var(--text);
  transition: background .25s, color .25s;
}

.ai-chat-container.light {
  --bg: #f9f9f9;
  --panel: #ffffff;
  --panel-border: #e5e5e5;
  --text: #2d333a;
  --text-secondary: #6b7280;
  --bubble-ai: #f7f7f8;
  --bubble-border: #e5e5e5;
}

.ai-chat-container.dark {
  --bg: #131415;
  --panel: #1d1f21;
  --panel-border: #2a2d31;
  --text: #e9ecef;
  --text-secondary: #9ca3af;
  --bubble-ai: #232528;
  --bubble-border: #33383d;
}

.sidebar {
  width: 260px;
  background: #171717;
  color: white;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #2a2a2a;
  transition: left .3s;
}

@media (max-width: 900px) {
  .sidebar {
    position: fixed;
    left: -260px;
    top: 0;
    bottom: 0;
    z-index: 1100;
  }
  .sidebar.open {
    left: 0;
  }
}

.sidebar-header {
  padding: 16px;
  border-bottom: 1px solid #2a2a2a;
}

.new-chat-btn {
  width: 100%;
  background: transparent;
  border: 1px solid #404040;
  color: white;
  padding: 12px 16px;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  transition: all 0.2s;
}
.new-chat-btn:hover {
  background: #2a2a2a;
}
.new-chat-btn .icon {
  font-size: 16px;
  font-weight: bold;
}

.chat-history {
  flex: 1;
  padding: 8px;
  overflow-y: auto;
}
.history-item {
  padding: 12px 16px;
  margin: 2px 0;
  border-radius: 8px;
  cursor: pointer;
  transition: all .2s;
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 14px;
  color: #ececf1;
}
.history-item:hover {
  background: #2a2a2a;
}
.history-item.active {
  background: #343541;
}
.chat-time {
  font-size: 12px;
  color: #8e8ea0;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid #2a2a2a;
}
.user-profile {
  display: flex;
  align-items: center;
  gap: 12px;
}
.avatar {
  width: 32px;
  height: 32px;
  background: #10a37f;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
}

.main-chat {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: var(--panel);
  border-left: 1px solid var(--panel-border);
}

.chat-header {
  height: 60px;
  background: var(--panel);
  border-bottom: 1px solid var(--panel-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.sidebar-toggle,
.header-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  color: var(--text);
  transition: background .15s;
  font-size: 16px;
}
.sidebar-toggle:hover,
.header-btn:hover {
  background: rgba(0,0,0,0.06);
}
.ai-chat-container.dark .sidebar-toggle:hover,
.ai-chat-container.dark .header-btn:hover {
  background: rgba(255,255,255,0.08);
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  position: relative;
}

.welcome-screen {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}
.welcome-content {
  text-align: center;
  max-width: 800px;
}
.logo-circle {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg,#10a37f,#1a7f64);
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  font-weight: 700;
}
.welcome-content h2 {
  font-size: 28px;
  margin: 16px 0 8px;
  font-weight: 600;
  color: var(--text);
}
.welcome-content p {
  margin: 0 0 32px;
  color: var(--text-secondary);
}

.example-prompts {
  display: grid;
  grid-template-columns: repeat(auto-fit,minmax(240px,1fr));
  gap: 16px;
}
.prompt-card {
  background: var(--bubble-ai);
  border: 1px solid var(--bubble-border);
  border-radius: 12px;
  padding: 18px;
  display: flex;
  gap: 16px;
  cursor: pointer;
  transition: background .2s, border-color .2s;
}
.prompt-card:hover {
  background: rgba(16,163,127,0.08);
  border-color: #10a37f;
}
.ai-chat-container.dark .prompt-card:hover {
  background: rgba(16,163,127,0.15);
}

.messages-list {
  padding: 20px;
  max-width: 880px;
  margin: 0 auto;
}

.message {
  margin-bottom: 26px;
}
.message-content {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}
.user-message .message-content {
  flex-direction: row-reverse;
}
.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display:flex;
  align-items:center;
  justify-content:center;
  font-weight:600;
  font-size:16px;
  flex-shrink:0;
  color:#fff;
}
.user-avatar {
  background:#10a37f;
}
.ai-avatar {
  background:#da70d6;
}

.message-bubble {
  max-width: 70%;
  min-width: 180px;
  padding:16px 18px;
  border-radius:18px;
  position: relative;
  line-height:1.55;
  font-size:15px;
  word-break: break-word;
}

.user-bubble {
  background:#10a37f;
  color:#fff;
}
.ai-bubble {
  background: var(--bubble-ai);
  color: var(--text);
  border: 1px solid var(--bubble-border);
}

.message-time {
  display:block;
  font-size:12px;
  opacity:.6;
  margin-top:10px;
}

.typing-indicator {
  display:flex;
  gap:5px;
}
.typing-indicator span {
  width:8px;
  height:8px;
  background:#9ca3af;
  border-radius:50%;
  animation: typing 1.4s infinite ease-in-out;
}
.typing-indicator span:nth-child(2){animation-delay:.2s}
.typing-indicator span:nth-child(3){animation-delay:.4s}
@keyframes typing {
  0%,80%,100% {transform:scale(.6);opacity:.5}
  40% {transform:scale(1);opacity:1}
}

.input-area {
  background: var(--panel);
  border-top:1px solid var(--panel-border);
  padding:20px;
}
.input-container {
  max-width: 880px;
  margin:0 auto;
}
.input-wrapper {
  display:flex;
  align-items:flex-end;
  background: var(--panel);
  border:1px solid var(--panel-border);
  border-radius:14px;
  padding:10px 12px;
  gap:12px;
  box-shadow:0 2px 4px rgba(0,0,0,0.06);
  transition:border-color .2s, box-shadow .2s;
}
.input-wrapper:focus-within {
  border-color:#10a37f;
  box-shadow:0 0 0 2px rgba(16,163,127,.15);
}
.input-wrapper.disabled {
  opacity:.7;
  pointer-events:none;
}

.message-input {
  flex:1;
  resize:none;
  border:none;
  outline:none;
  background:transparent;
  font-size:15px;
  color: var(--text);
  line-height:1.5;
  max-height:140px;
  overflow-y:auto;
}
.message-input::placeholder {
  color: var(--text-secondary);
}

.send-button {
  width:40px;
  height:40px;
  background:#10a37f;
  border:none;
  border-radius:10px;
  color:#fff;
  cursor:pointer;
  display:flex;
  align-items:center;
  justify-content:center;
  transition: background .2s, transform .15s;
}
.send-button:hover {
  background:#0d8a6a;
}
.send-button:disabled {
  background:#a5b2b5;
  cursor:not-allowed;
}

.input-footer {
  text-align:center;
  margin-top:10px;
}
.input-footer small {
  font-size:12px;
  color: var(--text-secondary);
}

@media (max-width: 640px){
  .message-bubble { max-width: 85%; }
  .welcome-content h2 { font-size: 22px; }
  .example-prompts { grid-template-columns: 1fr; }
}
</style>