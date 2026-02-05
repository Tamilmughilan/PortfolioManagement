import React, { useMemo, useState } from 'react';
import { MessageCircle, X, Send } from 'lucide-react';
import { sendChat } from '../services/api';
import '../styles/ChatWidget.css';

const ChatWidget = ({ portfolioId }) => {
  const [open, setOpen] = useState(false);
  const [messages, setMessages] = useState([
    { role: 'assistant', text: 'Hi! Ask me about stocks, markets, or your portfolio.' }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);

  const historyPayload = useMemo(() => {
    // Keep last 12 turns to control prompt size
    const last = messages.slice(-12);
    return last.map(m => ({
      role: m.role === 'assistant' ? 'assistant' : 'user',
      content: m.text
    }));
  }, [messages]);

  const handleSend = async () => {
    const text = input.trim();
    if (!text) return;
    setInput('');
    setMessages(prev => [...prev, { role: 'user', text }]);
    try {
      setLoading(true);
      const response = await sendChat({
        message: text,
        portfolioId,
        history: historyPayload
      });
      setMessages(prev => [...prev, { role: 'assistant', text: response.data.reply }]);
    } catch (err) {
      setMessages(prev => [
        ...prev,
        { role: 'assistant', text: 'Sorry, I could not reach the assistant right now.' }
      ]);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className={`chat-widget ${open ? 'open' : ''}`}>
      {open && (
        <div className="chat-panel">
          <div className="chat-header">
            <div>
              <h4>Stock Assistant</h4>
              <span>General + light portfolio context</span>
            </div>
            <button className="chat-close" onClick={() => setOpen(false)}>
              <X size={18} />
            </button>
          </div>
          <div className="chat-body">
            {messages.map((msg, index) => (
              <div key={index} className={`chat-bubble ${msg.role}`}>
                {msg.text}
              </div>
            ))}
            {loading && <div className="chat-bubble assistant">Typing...</div>}
          </div>
          <div className="chat-input">
            <textarea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Ask about stocks, markets, or portfolio..."
              rows={2}
            />
            <button onClick={handleSend}>
              <Send size={18} />
            </button>
          </div>
        </div>
      )}
      {!open && (
        <button className="chat-toggle" onClick={() => setOpen(true)}>
          <MessageCircle size={20} />
          Chat
        </button>
      )}
    </div>
  );
};

export default ChatWidget;