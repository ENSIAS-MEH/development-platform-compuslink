import { useState, useEffect, useRef } from "react";
import { useLocation } from "react-router-dom";
import api from "../services/api";
import { useAuth } from "../context/AuthContext";

interface Conversation {
  id: string;
  otherUserId: string;
  otherUserName: string;
  otherUserProfilePic: string | null;
  lastMessage: string;
  lastMessageAt: string;
}

interface Message {
  id: string;
  conversationId: string;
  senderId: string;
  senderName: string;
  content: string;
  createdAt: string;
  isRead: boolean;
}

export default function MessagingPage() {
  const { user } = useAuth();
  const location = useLocation();
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
  const [messages, setMessages] = useState<Message[]>([]);
  const [messageInput, setMessageInput] = useState("");
  const [loading, setLoading] = useState(true);
  const [sendingMessage, setSendingMessage] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const initialMessageAttemptedRef = useRef(false);

  const locationState = location.state as { sellerId?: string; itemName?: string } | null;

  useEffect(() => {
    fetchConversations();

    // Poll for new conversations every 3 seconds
    const pollInterval = setInterval(() => {
      fetchConversations();
    }, 3000);

    return () => clearInterval(pollInterval);
  }, []);

  useEffect(() => {
    if (selectedConversation) {
      fetchMessages(selectedConversation.id);

      // Poll for new messages every 2 seconds
      const pollInterval = setInterval(() => {
        fetchMessages(selectedConversation.id);
      }, 2000);

      return () => clearInterval(pollInterval);
    }
  }, [selectedConversation]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    if (!locationState?.sellerId || !locationState?.itemName) return;
    if (initialMessageAttemptedRef.current) return;
    if (loading) return;

    const sellerConversation = conversations.find(
      (conv) => conv.otherUserId === locationState.sellerId
    );

    if (!sellerConversation) {
      // Conversation doesn't exist yet, send initial message to create it
      initialMessageAttemptedRef.current = true;
      const initialMessage = `Bonjour, je m'intéresse à ${locationState.itemName}`;
      sendInitialMessage(initialMessage, locationState.sellerId);
    }
  }, [locationState, conversations, loading]);

  const fetchConversations = async () => {
    try {
      setLoading(true);
      const response = await api.get("/messages/conversations");
      setConversations(response.data);

      if (locationState?.sellerId) {
        // Looking for a specific seller conversation
        const sellerConversation = response.data.find(
          (conv: Conversation) => conv.otherUserId === locationState.sellerId
        );
        if (sellerConversation) {
          setSelectedConversation(sellerConversation);
        }
        // If conversation doesn't exist yet, leave selectedConversation null
      } else if (response.data.length > 0 && !selectedConversation) {
        // Normal case: select first conversation
        setSelectedConversation(response.data[0]);
      }
    } catch (err) {
      console.error("Erreur lors du chargement des conversations", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchMessages = async (conversationId: string) => {
    try {
      const response = await api.get(`/messages/conversations/${conversationId}`);
      setMessages(response.data);
    } catch (err) {
      console.error("Erreur lors du chargement des messages", err);
    }
  };

  const sendInitialMessage = async (message: string, recipientId: string) => {
    try {
      await api.post(`/messages/send/${recipientId}`, {
        content: message,
      });
      // Refresh conversations to include the newly created one
      fetchConversations();
    } catch (err) {
      console.error("Erreur lors de l'envoi du message initial", err);
    }
  };

  const handleSendMessage = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!messageInput.trim() || !selectedConversation || sendingMessage) return;

    setSendingMessage(true);
    try {
      const response = await api.post(`/messages/send/${selectedConversation.otherUserId}`, {
        content: messageInput,
      });
      setMessages([...messages, response.data]);
      setMessageInput("");
      fetchConversations();
    } catch (err) {
      console.error("Erreur lors de l'envoi du message", err);
    } finally {
      setSendingMessage(false);
    }
  };

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  const formatTime = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleTimeString("fr-FR", { hour: "2-digit", minute: "2-digit" });
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    const today = new Date();
    if (date.toDateString() === today.toDateString()) {
      return formatTime(dateString);
    }
    return date.toLocaleDateString("fr-FR", { month: "short", day: "numeric" });
  };

  return (
    <div className="max-w-7xl mx-auto px-8 py-12 flex flex-col" style={{ height: "calc(100vh - 64px - 200px)" }}>
      <h1 className="text-4xl font-bold font-[Geist] mb-8">Messages</h1>

      <div className="flex gap-6 flex-1 min-h-0 overflow-hidden">
        {/* Conversations List */}
        <div className="w-80 bg-white rounded-2xl border border-gray-100 overflow-hidden flex flex-col">
          <div className="p-4 border-b border-gray-100">
            <h2 className="font-semibold text-lg">Conversations</h2>
          </div>

          {loading ? (
            <div className="flex items-center justify-center flex-1 text-gray-500">
              Chargement...
            </div>
          ) : conversations.length === 0 ? (
            <div className="flex items-center justify-center flex-1 text-gray-500">
              Aucune conversation
            </div>
          ) : (
            <div className="overflow-y-auto flex-1">
              {conversations.map((conv) => (
                <button
                  key={conv.id}
                  onClick={() => setSelectedConversation(conv)}
                  className={`w-full p-4 border-b border-gray-50 text-left transition-colors hover:bg-gray-50 ${
                    selectedConversation?.id === conv.id ? "bg-primary/5" : ""
                  }`}
                >
                  <div className="flex gap-3">
                    <div className="w-12 h-12 rounded-full bg-primary text-white flex items-center justify-center font-bold flex-shrink-0">
                      {conv.otherUserProfilePic ? (
                        <img
                          src={conv.otherUserProfilePic}
                          alt={conv.otherUserName}
                          className="w-full h-full rounded-full object-cover"
                        />
                      ) : (
                        conv.otherUserName.charAt(0).toUpperCase()
                      )}
                    </div>
                    <div className="flex-1 min-w-0">
                      <p className="font-semibold text-sm">{conv.otherUserName}</p>
                      <p className="text-xs text-gray-500 truncate">{conv.lastMessage || "Aucun message"}</p>
                      <p className="text-xs text-gray-400 mt-1">
                        {conv.lastMessageAt ? formatDate(conv.lastMessageAt) : ""}
                      </p>
                    </div>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        {/* Messages Area */}
        <div className="flex-1 bg-white rounded-2xl border border-gray-100 overflow-hidden flex flex-col">
          {selectedConversation ? (
            <>
              {/* Header */}
              <div className="p-4 border-b border-gray-100 flex items-center gap-3">
                <div className="w-10 h-10 rounded-full bg-primary text-white flex items-center justify-center font-bold flex-shrink-0">
                  {selectedConversation.otherUserProfilePic ? (
                    <img
                      src={selectedConversation.otherUserProfilePic}
                      alt={selectedConversation.otherUserName}
                      className="w-full h-full rounded-full object-cover"
                    />
                  ) : (
                    selectedConversation.otherUserName.charAt(0).toUpperCase()
                  )}
                </div>
                <div>
                  <p className="font-semibold">{selectedConversation.otherUserName}</p>
                </div>
              </div>

              {/* Messages */}
              <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
                {messages.length === 0 ? (
                  <div className="flex items-center justify-center h-full text-gray-500">
                    Commencez la conversation
                  </div>
                ) : (
                  messages.map((msg) => {
                    const isCurrentUser = String(msg.senderId) === String(user?.userId);
                    return (
                      <div
                        key={msg.id}
                        className={`flex ${isCurrentUser ? "justify-end" : "justify-start"}`}
                      >
                        <div
                          className={`max-w-xs px-4 py-2 rounded-2xl ${
                            isCurrentUser
                              ? "bg-primary text-white rounded-br-none"
                              : "bg-white border border-gray-200 rounded-bl-none"
                          }`}
                        >
                          <p className="text-sm break-words">{msg.content}</p>
                          <p
                            className={`text-xs mt-1 ${
                              isCurrentUser
                                ? "text-white/70"
                                : "text-gray-400"
                            }`}
                          >
                            {formatTime(msg.createdAt)}
                          </p>
                        </div>
                      </div>
                    );
                  })
                )}
                <div ref={messagesEndRef} />
              </div>

              {/* Input */}
              <form onSubmit={handleSendMessage} className="p-4 border-t border-gray-100 flex gap-2">
                <input
                  type="text"
                  value={messageInput}
                  onChange={(e) => setMessageInput(e.target.value)}
                  placeholder="Écrivez un message..."
                  className="flex-1 px-4 py-3 border border-gray-200 rounded-xl text-sm outline-none focus:ring-2 focus:ring-primary/20 focus:border-primary"
                />
                <button
                  type="submit"
                  disabled={sendingMessage || !messageInput.trim()}
                  className="px-6 py-3 bg-primary text-white rounded-xl text-sm font-medium hover:bg-primary-dark transition-colors disabled:opacity-50"
                >
                  <span className="material-symbols-outlined">send</span>
                </button>
              </form>
            </>
          ) : (
            <div className="flex items-center justify-center h-full text-gray-500">
              Sélectionnez une conversation
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
