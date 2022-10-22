package com.Guo.GuoSend.service.impl;

import com.Guo.GuoSend.entity.AddressBook;
import com.Guo.GuoSend.mapper.AddressBookMapper;
import com.Guo.GuoSend.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
