package cn.alumik.shop.service;

import cn.alumik.shop.dao.ItemRepository;
import cn.alumik.shop.dao.UserRepository;
import cn.alumik.shop.entity.Category;
import cn.alumik.shop.entity.Item;
import cn.alumik.shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService{
    private ItemRepository itemRepository;
    private SecurityService securityService;
    private UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, SecurityService securityService, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.securityService = securityService;
        this.userRepository = userRepository;
    }

    @Override
    public void save(Item item, String filename) {
        String username = securityService.findLoggedInUsername();
        User user = userRepository.findByUsername(username);
        item.setSeller(user);
        item.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        item.setModifiedAt(new Timestamp(System.currentTimeMillis()));
        item.setAvailable(true);
        item.setPic(filename);
        itemRepository.save(item);
    }

    @Override
    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item getById(int id) {
        return itemRepository.findById(id).get();
    }

    @Override
    public Page<Object []> findAll(String name, int pageNum, int pageSize, Sort sort){
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        return itemRepository.findAllByNameContainsSell(name, pageable);
    }

    @Override
    public List<Object[]> findAllOrderByRand(String name, int size) {
        return itemRepository.findAllByNameContainsSellOrderByRand(name, size);
    }

    @Override
    public Page<Item> findAllBySeller(int pageNum, int pageSize, Sort sort){
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        User user = userRepository.findByUsername(securityService.findLoggedInUsername());
        return itemRepository.findAllBySeller(user, pageable);
    }

    @Override
    public Optional<Item> findById(Integer id) {
        return itemRepository.findById(id);
    }

    @Override
    public void toggleAvailable(Integer id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setAvailable(!item.getAvailable());
            itemRepository.save(item);
        }
    }
}
