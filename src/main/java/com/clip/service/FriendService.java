package com.clip.service;

import com.clip.config.exception.*;
import com.clip.dto.friend.SendFriendRequestDTO;
import com.clip.dto.friend.SendFriendResponseDTO;
import com.clip.entity.Friend;
import com.clip.entity.User;
import com.clip.entity.enums.FriendStatusType;
import com.clip.repository.FriendRepository;
import com.clip.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    @Transactional
    public SendFriendResponseDTO sendFriendRequest(String fromId, SendFriendRequestDTO request){
        // 닉네임으로 상대 유저 조회
        User toUser = userRepository.findByNickName(request.getNickName())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "해당 유저가 존재하지 않습니다"));

        // 본인에게 친구 요청 방지
        if (toUser.getUserId().equals(fromId)) {
            throw new CustomException("SEND_MYSELF", "자기 자신에게 친구요청 할 수 없습니다");
        }

        // 친구 요청이 예외 사항 로직
        Optional<Friend> friendRequestStatus = friendRepository.findFriendRelation(fromId, toUser.getUserId());

        if (friendRequestStatus.isPresent()) {
            Friend friend = friendRequestStatus.get();
            switch (friend.getStatus()) {
                case REQUEST:
                    throw new CustomException("AlREADY_REQIESTED" ,"이미 친구 요청을 보냈거나 받은 상태입니다.");
                case ACCEPT:
                    throw new CustomException("AlREADY_FRIENDS" ,"이미 친구 상태입니다.");
            }
        }

        // 저장
        Friend friend = Friend.builder()
                .fromId(fromId)
                .toId(toUser.getUserId())
                .status(FriendStatusType.REQUEST)
                .build();

        friendRepository.save(friend);

        return new SendFriendResponseDTO("친구 요청이 성공적으로 전송되었습니다.");
    }

}
