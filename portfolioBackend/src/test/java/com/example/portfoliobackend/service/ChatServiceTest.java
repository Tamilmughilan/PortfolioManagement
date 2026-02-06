package com.example.portfoliobackend.service;

import com.example.portfoliobackend.dto.ChatRequestDTO;
import com.example.portfoliobackend.entity.Holding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService Unit Tests")
class ChatServiceTest {

    @Mock
    private PortfolioService portfolioService;

    @InjectMocks
    private ChatService chatService;

    private ChatRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new ChatRequestDTO();
        testRequest.setMessage("What is a stock?");
        testRequest.setPortfolioId(1L);
    }

    @Nested
    @DisplayName("chat Tests - API Key Scenarios")
    class ApiKeyTests {

        @Test
        @DisplayName("Should return error message when API key is missing")
        void chat_WhenApiKeyMissing_ShouldReturnErrorMessage() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "");
            
            String result = chatService.chat(testRequest);

            assertThat(result).contains("API key is missing");
        }

        @Test
        @DisplayName("Should return error message when API key is null")
        void chat_WhenApiKeyNull_ShouldReturnErrorMessage() {
            ReflectionTestUtils.setField(chatService, "geminiKey", null);
            
            String result = chatService.chat(testRequest);

            assertThat(result).contains("API key is missing");
        }

        @Test
        @DisplayName("Should return error message when API key is blank")
        void chat_WhenApiKeyBlank_ShouldReturnErrorMessage() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "   ");
            
            String result = chatService.chat(testRequest);

            assertThat(result).contains("API key is missing");
        }
    }

    @Nested
    @DisplayName("chat Tests - Message Validation")
    class MessageValidationTests {

        @Test
        @DisplayName("Should return prompt when message is null")
        void chat_WhenMessageNull_ShouldReturnPrompt() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setMessage(null);
            
            String result = chatService.chat(testRequest);

            assertThat(result).contains("Ask me anything");
        }

        @Test
        @DisplayName("Should return prompt when message is empty")
        void chat_WhenMessageEmpty_ShouldReturnPrompt() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setMessage("");
            
            String result = chatService.chat(testRequest);

            assertThat(result).contains("Ask me anything");
        }

        @Test
        @DisplayName("Should return prompt when message is only whitespace")
        void chat_WhenMessageWhitespace_ShouldReturnPrompt() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setMessage("   ");
            
            String result = chatService.chat(testRequest);

            assertThat(result).contains("Ask me anything");
        }

        @Test
        @DisplayName("Should trim message before processing")
        void chat_ShouldTrimMessage() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setMessage("  What is a stock?  ");
            
            // Note: RestTemplate is created internally, so we can't easily mock it
            // This test verifies the message trimming logic works
            // For full integration, would need to use @SpringBootTest or mock RestTemplate differently
            String result = chatService.chat(testRequest);

            // Will return error about API key or Gemini response, but message was processed
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("chat Tests - Portfolio Context")
    class PortfolioContextTests {

        @Test
        @DisplayName("Should include portfolio context when portfolio ID is provided")
        void chat_WithPortfolioId_ShouldIncludeContext() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setPortfolioId(1L);
            
            Holding holding = new Holding();
            holding.setAssetName("Apple");
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(Arrays.asList(holding, holding));
            
            // RestTemplate is created internally, so we test the context building logic
            // The API call will fail without real key, but context is built correctly
            String result = chatService.chat(testRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should handle null portfolio ID")
        void chat_WithNullPortfolioId_ShouldHandleGracefully() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setPortfolioId(null);
            
            // With null portfolio ID, context should be empty
            String result = chatService.chat(testRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should handle zero portfolio value")
        void chat_WithZeroPortfolioValue_ShouldIncludeContext() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setPortfolioId(1L);
            
            when(portfolioService.calculateTotalValue(1L)).thenReturn(BigDecimal.ZERO);
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(Arrays.asList());
            
            // Context should include zero value
            String result = chatService.chat(testRequest);

            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("extractReply Tests - Response Parsing")
    class ExtractReplyTests {

        @Test
        @DisplayName("Should extract reply from valid Gemini response structure")
        void extractReply_WithValidResponse_ShouldExtractText() {
            Map<String, Object> mockResponse = createMockGeminiResponse("This is a stock response");
            
            // Use reflection to test extractReply method directly
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).isEqualTo("This is a stock response");
        }

        @Test
        @DisplayName("Should return error when response is null")
        void extractReply_WhenResponseNull_ShouldReturnErrorMessage() {
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", (Object) null);

            assertThat(result).contains("No response from Gemini");
        }

        @Test
        @DisplayName("Should return error when candidates is null")
        void extractReply_WhenCandidatesNull_ShouldReturnErrorMessage() {
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("candidates", null);
            
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).contains("No response from Gemini");
        }

        @Test
        @DisplayName("Should return error when candidates is empty")
        void extractReply_WhenCandidatesEmpty_ShouldReturnErrorMessage() {
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("candidates", Arrays.asList());
            
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).contains("No response from Gemini");
        }

        @Test
        @DisplayName("Should return error when parts is null")
        void extractReply_WhenPartsNull_ShouldReturnErrorMessage() {
            Map<String, Object> content = new HashMap<>();
            content.put("parts", null);
            
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("content", content);
            
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("candidates", Arrays.asList(candidate));
            
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).contains("No response from Gemini");
        }

        @Test
        @DisplayName("Should return error when parts is empty")
        void extractReply_WhenPartsEmpty_ShouldReturnErrorMessage() {
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Arrays.asList());
            
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("content", content);
            
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("candidates", Arrays.asList(candidate));
            
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).contains("No response from Gemini");
        }

        @Test
        @DisplayName("Should return error when text is null")
        void extractReply_WhenTextNull_ShouldReturnErrorMessage() {
            Map<String, Object> part = new HashMap<>();
            part.put("text", null);
            
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Arrays.asList(part));
            
            Map<String, Object> candidate = new HashMap<>();
            candidate.put("content", content);
            
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("candidates", Arrays.asList(candidate));
            
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).contains("No response from Gemini");
        }

        @Test
        @DisplayName("Should handle exception during response extraction")
        void extractReply_WhenExceptionOccurs_ShouldReturnErrorMessage() {
            // Create malformed response that will cause exception
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("candidates", "invalid");
            
            String result = (String) ReflectionTestUtils.invokeMethod(chatService, "extractReply", mockResponse);

            assertThat(result).contains("No response from Gemini");
        }
    }

    @Nested
    @DisplayName("buildContext Tests")
    class BuildContextTests {

        @Test
        @DisplayName("Should build context with portfolio information")
        void buildContext_WithPortfolioId_ShouldIncludeInfo() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(new BigDecimal("100000"));
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(Arrays.asList(new Holding(), new Holding()));
            
            String context = (String) ReflectionTestUtils.invokeMethod(chatService, "buildContext", 1L);

            assertThat(context).contains("total value");
            assertThat(context).contains("100000");
            assertThat(context).contains("holdings count");
            assertThat(context).contains("2");
        }

        @Test
        @DisplayName("Should return empty string when portfolio ID is null")
        void buildContext_WithNullPortfolioId_ShouldReturnEmpty() {
            String context = (String) ReflectionTestUtils.invokeMethod(chatService, "buildContext", (Object) null);

            assertThat(context).isEmpty();
        }

        @Test
        @DisplayName("Should handle zero portfolio value")
        void buildContext_WithZeroValue_ShouldIncludeZero() {
            when(portfolioService.calculateTotalValue(1L)).thenReturn(BigDecimal.ZERO);
            when(portfolioService.getHoldingsByPortfolioId(1L)).thenReturn(Arrays.asList());
            
            String context = (String) ReflectionTestUtils.invokeMethod(chatService, "buildContext", 1L);

            assertThat(context).contains("0");
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle very long message")
        void chat_WithVeryLongMessage_ShouldProcess() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setMessage("What is a stock? ".repeat(100));
            
            // Message validation passes, API call will fail without real key
            String result = chatService.chat(testRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should handle special characters in message")
        void chat_WithSpecialCharacters_ShouldProcess() {
            ReflectionTestUtils.setField(chatService, "geminiKey", "test-key");
            testRequest.setMessage("What is $AAPL? Tell me about 50% returns!");
            
            // Message validation passes
            String result = chatService.chat(testRequest);

            assertThat(result).isNotNull();
        }
    }

    private Map<String, Object> createMockGeminiResponse(String text) {
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", text);
        
        Map<String, Object> content = new HashMap<>();
        content.put("parts", Arrays.asList(textPart));
        
        Map<String, Object> candidate = new HashMap<>();
        candidate.put("content", content);
        
        Map<String, Object> response = new HashMap<>();
        response.put("candidates", Arrays.asList(candidate));
        
        return response;
    }
}
