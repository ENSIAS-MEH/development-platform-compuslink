package com.compuslink.marketplace.service;

import com.compuslink.common.dto.UserSummaryDTO;
import com.compuslink.common.exception.EntityNotFoundException;
import com.compuslink.marketplace.client.UserClient;
import com.compuslink.marketplace.dto.*;
import com.compuslink.marketplace.model.*;
import com.compuslink.marketplace.repository.ItemImageRepository;
import com.compuslink.marketplace.repository.ItemInterestRepository;
import com.compuslink.marketplace.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemImageRepository imageRepository;

    @Mock
    private ItemInterestRepository interestRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ItemService itemService;

    private UUID testSellerId;
    private UUID testItemId;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testSellerId = UUID.randomUUID();
        testItemId = UUID.randomUUID();

        testItem = new Item();
        testItem.setId(testItemId);
        testItem.setSellerId(testSellerId);
        testItem.setTitle("Test Item");
        testItem.setDescription("A test item");
        testItem.setPrice(new BigDecimal("50.00"));
        testItem.setCity("Test City");
        testItem.setCondition(ItemCondition.NEW);
        testItem.setCategory("Electronics");
        testItem.setStatus(ItemStatus.OPEN);
    }

    @Test
    void testCreateItemSuccess() {
        ItemCreateRequest request = new ItemCreateRequest();
        request.setTitle("New Item");
        request.setDescription("Item description");
        request.setPrice(new BigDecimal("100.00"));
        request.setCity("Test City");
        request.setCondition(ItemCondition.LIKE_NEW);
        request.setCategory("Books");
        request.setImages(null);

        when(itemRepository.save(any(Item.class))).thenReturn(testItem);

        ItemResponse response = itemService.createItem(request, testSellerId);

        assertNotNull(response);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testCreateItemWithImages() {
        MultipartFile mockFile = mock(MultipartFile.class);
        List<MultipartFile> images = List.of(mockFile);

        ItemCreateRequest request = new ItemCreateRequest();
        request.setTitle("Item with images");
        request.setImages(images);

        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(fileStorageService.store(mockFile)).thenReturn("http://localhost/uploads/img.jpg");
        when(imageRepository.save(any(ItemImage.class))).thenReturn(new ItemImage());

        ItemResponse response = itemService.createItem(request, testSellerId);

        assertNotNull(response);
        verify(fileStorageService).store(mockFile);
        verify(imageRepository).save(any(ItemImage.class));
    }

    @Test
    void testGetItemSuccess() {
        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));
        when(imageRepository.findByItemOrderBySortOrderAsc(testItem)).thenReturn(List.of());
        when(userClient.getUserSummary(testSellerId)).thenReturn(new UserSummaryDTO(testSellerId, "Seller Name", "seller@test.com", null));

        ItemResponse response = itemService.getItem(testItemId);

        assertNotNull(response);
        assertEquals(testItemId, response.getId());
        verify(itemRepository).findById(testItemId);
    }

    @Test
    void testGetItemNotFound() {
        when(itemRepository.findById(testItemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(testItemId));
    }

    @Test
    void testUpdateItemSuccess() {
        ItemUpdateRequest request = new ItemUpdateRequest();
        request.setTitle("Updated Title");
        request.setPrice(new BigDecimal("150.00"));

        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));
        when(imageRepository.findByItemOrderBySortOrderAsc(testItem)).thenReturn(List.of());
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(userClient.getUserSummary(testSellerId)).thenReturn(new UserSummaryDTO(testSellerId, "Seller", "seller@test.com", null));

        ItemResponse response = itemService.updateItem(testItemId, request, testSellerId);

        assertNotNull(response);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testUpdateItemNotSeller() {
        UUID buyerId = UUID.randomUUID();
        ItemUpdateRequest request = new ItemUpdateRequest();

        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));

        assertThrows(IllegalArgumentException.class, () -> itemService.updateItem(testItemId, request, buyerId));
    }

    @Test
    void testCloseItemSuccess() {
        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));

        itemService.closeItem(testItemId, testSellerId);

        assertEquals(ItemStatus.CLOSED, testItem.getStatus());
        verify(itemRepository).save(testItem);
    }

    @Test
    void testCloseItemNotSeller() {
        UUID buyerId = UUID.randomUUID();
        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));

        assertThrows(IllegalArgumentException.class, () -> itemService.closeItem(testItemId, buyerId));
    }

    @Test
    void testMarkAsSoldSuccess() {
        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));
        when(imageRepository.findByItemOrderBySortOrderAsc(testItem)).thenReturn(List.of());
        when(itemRepository.save(any(Item.class))).thenReturn(testItem);
        when(userClient.getUserSummary(testSellerId)).thenReturn(new UserSummaryDTO(testSellerId, "Seller", "seller@test.com", null));

        ItemResponse response = itemService.markAsSold(testItemId, testSellerId);

        assertNotNull(response);
        assertEquals(ItemStatus.SOLD, testItem.getStatus());
        verify(itemRepository).save(testItem);
    }

    @Test
    void testExpressInterestSuccess() {
        UUID buyerId = UUID.randomUUID();
        ItemInterestRequest request = new ItemInterestRequest();
        request.setMessage("Interested in this item");

        ItemInterest interest = new ItemInterest();
        interest.setItemId(testItemId);
        interest.setUserId(buyerId);
        interest.setMessage("Interested in this item");

        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));
        when(interestRepository.existsByItemIdAndUserId(testItemId, buyerId)).thenReturn(false);
        when(interestRepository.save(any(ItemInterest.class))).thenReturn(interest);
        when(userClient.getUserSummary(buyerId)).thenReturn(new UserSummaryDTO(buyerId, "Buyer", "buyer@test.com", null));

        ItemInterestResponse response = itemService.expressInterest(testItemId, request, buyerId);

        assertNotNull(response);
        assertEquals("Interested in this item", response.getMessage());
        verify(interestRepository).save(any(ItemInterest.class));
    }

    @Test
    void testExpressInterestOwnItem() {
        ItemInterestRequest request = new ItemInterestRequest();
        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));

        assertThrows(IllegalArgumentException.class, () -> itemService.expressInterest(testItemId, request, testSellerId));
    }

    @Test
    void testExpressInterestAlreadyExpressed() {
        UUID buyerId = UUID.randomUUID();
        ItemInterestRequest request = new ItemInterestRequest();

        when(itemRepository.findById(testItemId)).thenReturn(Optional.of(testItem));
        when(interestRepository.existsByItemIdAndUserId(testItemId, buyerId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> itemService.expressInterest(testItemId, request, buyerId));
    }

    @Test
    void testGetMyItemsSuccess() {
        when(itemRepository.findBySellerId(testSellerId)).thenReturn(List.of(testItem));
        when(imageRepository.findFirstByItemAndIsCoverTrue(testItem)).thenReturn(Optional.empty());

        List<ItemSummaryResponse> responses = itemService.getMyItems(testSellerId);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(itemRepository).findBySellerId(testSellerId);
    }

    @Test
    void testExistsById() {
        when(itemRepository.existsById(testItemId)).thenReturn(true);

        boolean exists = itemService.existsById(testItemId);

        assertTrue(exists);
    }
}
