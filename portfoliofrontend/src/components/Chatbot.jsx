import React, { useState } from 'react';
import '../styles/Chatbot.css';
import '../styles/Chatbot.css';

const Chatbot = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [showDisclaimer, setShowDisclaimer] = useState(true);
  const [messages, setMessages] = useState([
    { id: 1, text: "Hello! I'm your Portfolio Assistant. How can I help you today?", sender: 'bot', timestamp: new Date() }
  ]);
  const [inputMessage, setInputMessage] = useState('');

  const handleSendMessage = () => {
    if (!inputMessage.trim()) return;

    const newMessage = {
      id: messages.length + 1,
      text: inputMessage,
      sender: 'user',
      timestamp: new Date()
    };

    setMessages([...messages, newMessage]);
    setInputMessage('');

    // Dummy response
    setTimeout(() => {
      const botResponse = {
        id: messages.length + 2,
        text: "Thank you for your message! This is a dummy chatbot. The full implementation will be available soon.",
        sender: 'bot',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, botResponse]);
    }, 1000);
  };

  const handleAcceptDisclaimer = () => {
    setShowDisclaimer(false);
  };

  const handleRejectDisclaimer = () => {
    setIsOpen(false);
    setShowDisclaimer(true);
  };

  return (
    <>
      {/* Floating Chat Button */}
      <button
        className="chatbot-toggle"
        onClick={() => setIsOpen(true)}
        title="Chat with Portfolio Assistant"
      >
        Chat
      </button>

      {/* Chat Modal */}
      {isOpen && (
        <div className="chatbot-overlay" onClick={() => setIsOpen(false)}>
          <div className="chatbot-modal" onClick={e => e.stopPropagation()}>
            <div className="chatbot-header">
              <h3>Portfolio Assistant</h3>
              <button
                className="chatbot-close"
                onClick={() => setIsOpen(false)}
              >
                ×
              </button>
            </div>

            <div className="chatbot-content">
              {showDisclaimer ? (
                <div className="chatbot-disclaimer">
                  <h4>Important Disclaimer</h4>
                  <p>
                    This chatbot is for informational purposes only and does not constitute
                    financial advice. The information provided may not be accurate or complete.
                    Always consult with qualified financial professionals before making investment
                    decisions.
                  </p>
                  <p>
                    By continuing, you acknowledge that you understand this is a demo version
                    and the full chatbot functionality will be implemented later.
                  </p>
                  <div className="disclaimer-actions">
                    <button
                      className="disclaimer-accept"
                      onClick={handleAcceptDisclaimer}
                    >
                      I Understand
                    </button>
                    <button
                      className="disclaimer-reject"
                      onClick={handleRejectDisclaimer}
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ) : (
                <>
                  <div className="chatbot-messages">
                    {messages.map(message => (
                      <div
                        key={message.id}
                        className={`message ${message.sender === 'user' ? 'user-message' : 'bot-message'}`}
                      >
                        <div className="message-content">
                          {message.text}
                        </div>
                        <div className="message-timestamp">
                          {message.timestamp.toLocaleTimeString()}
                        </div>
                      </div>
                    ))}
                  </div>

                  <div className="chatbot-input">
                    <input
                      type="text"
                      value={inputMessage}
                      onChange={(e) => setInputMessage(e.target.value)}
                      onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                      placeholder="Type your message..."
                    />
                    <button
                      onClick={handleSendMessage}
                      disabled={!inputMessage.trim()}
                    >
                      Send
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
};

export default Chatbot;