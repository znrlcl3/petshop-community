package com.petshop.community.controller;


import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.petshop.community.dto.CodeDto;
import com.petshop.community.dto.LikeResponseDto;
import com.petshop.community.dto.PostCreateDto;
import com.petshop.community.dto.PostDto;
import com.petshop.community.dto.PostEditDto;
import com.petshop.community.dto.PostSearchRequest;
import com.petshop.community.security.CustomUserDetails;
import com.petshop.community.service.CodeService;
import com.petshop.community.service.CodeService.CategoryInfo;
import com.petshop.community.service.PostLikeService;
import com.petshop.community.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final PostService postService;
    private final CodeService codeService;
    private final PostLikeService postLikeService;
    
    /**
     * 
     * 
     */
    @GetMapping("/{categoryPath}/list")
    public String getPostList(@PathVariable String categoryPath,@ModelAttribute PostSearchRequest searchRequest,@PageableDefault(size = 20) Pageable pageable,Model model) {
    	
    	Map<String, CategoryInfo> categoryMap = codeService.getCategoryPathMap("BOARD_CATEGORY");
    	CategoryInfo categoryInfo = categoryMap.get(categoryPath);
    	
    	if (categoryInfo == null) {
            return "redirect:/board";
        }
    	
    	String categoryCode = categoryInfo.getCode();
	    String categoryName = categoryInfo.getName();
    	
	    log.info("게시판 목록 조회 - categoryCode={}, keyword={}, page={}, size={}",categoryCode, searchRequest.getKeyword(), pageable.getPageNumber(), pageable.getPageSize());

        // 게시판 카테고리 코드 설정
    	searchRequest.setCategoryCode(categoryCode);
        
        // 게시글 목록 조회
        Page<PostDto> posts = postService.getPosts(searchRequest, pageable);
        
        log.info("게시판 목록 조회 완료 - totalElements={}, totalPages={}",posts.getTotalElements(), posts.getTotalPages());
        
        model.addAttribute("posts", posts);
        model.addAttribute("categoryName", categoryName);
        model.addAttribute("categoryPath", categoryPath);
        model.addAttribute("searchRequest", searchRequest);
        model.addAttribute("title", categoryName);
        
        return "board/" + categoryPath + "/list";
    }
    
    /**
     *  게시판 디테일 페이지
     * 
     */
    @GetMapping("/{categoryPath}/view/{postId}")
    public String getPostDetail(@PathVariable String categoryPath,@PathVariable Long postId,HttpServletRequest request,Model model,@AuthenticationPrincipal CustomUserDetails user) {
        
    	Map<String, CategoryInfo> categoryMap = codeService.getCategoryPathMap("BOARD_CATEGORY");
        CategoryInfo categoryInfo = categoryMap.get(categoryPath);
        
        if (categoryInfo == null) {
            return "redirect:/board";
        }
        
        try {
        	String clientIp = getClientIp(request);
            log.info("게시글 상세 조회 시작 - categoryPath={}, postId={}, clientIp={}", categoryPath, postId, clientIp);
            PostDto post = postService.getPostDetail(postId, clientIp);
            
            // 게시글이 존재하지 않거나 삭제된 경우
            if (post == null || post.isDeleted()) {
                log.warn("존재하지 않는 게시글 - ID: {}", postId);
                return "redirect:/board/" + categoryPath + "/list?error=notFound";
            }
            
            // 카테고리 검증
            if (!categoryInfo.getCode().equals(post.getCategoryCode())) {
                log.warn("카테고리 불일치 - URL: {}, 게시글: {}", categoryPath, post.getCategoryCode());
                return "redirect:/board/" + categoryPath + "/list?error=invalidCategory";
            }
            
            // --- 좋아요 상태 로직 추가 ---
            boolean userHasLiked = false;
            if (user != null) {
                userHasLiked = postLikeService.hasUserLikedPost(postId, user.getMemberId());
            }
            model.addAttribute("userHasLiked", userHasLiked); // view.html 로 전달
            
            // --- 신고 사유 목록 추가 ---
            List<CodeDto> reportReasons = codeService.getCodesByGroup("REPORT_REASON");
            model.addAttribute("reportReasons", reportReasons);
            
            log.info("게시글 상세 조회 성공 - postId={}, title={}", postId, post.getTitle());

            model.addAttribute("post", post);
            model.addAttribute("categoryName", categoryInfo.getName());
            model.addAttribute("categoryPath", categoryPath);
            model.addAttribute("title", post.getTitle());
            
            log.info("뷰 렌더링 - message: {}", model.getAttribute("message"));
            
            return "board/" + categoryPath + "/view";
            
        } catch (Exception e) {
            log.error("게시글 조회 실패 - postId: {}, error: {}", postId, e.getMessage());
            return "redirect:/board/" + categoryPath + "/list?error=system";
        }
        
    }
    
    /**
     * 게시글 등록 폼 페이지
     */
    @GetMapping("/{categoryPath}/write")
    public String createPostForm(@PathVariable String categoryPath,HttpServletRequest request,Model model,Authentication authentication) {
        
        Map<String, CategoryInfo> categoryMap = codeService.getCategoryPathMap("BOARD_CATEGORY");
        CategoryInfo categoryInfo = categoryMap.get(categoryPath);
        
        if (categoryInfo == null) {
            return "redirect:/board";
        }
        
        log.info("게시글 쓰기 폼 조회 - categoryPath={}", categoryPath);
        
        log.info("게시글 작성 폼 조회 - categoryPath={}, user={}", 
                categoryPath, authentication.getName());
        
        // 빈 DTO 객체 생성 (유효성 검증용)
        model.addAttribute("postCreateDto", new PostCreateDto());
        model.addAttribute("categoryName", categoryInfo.getName());
        model.addAttribute("categoryPath", categoryPath);
        model.addAttribute("title", "새 글 작성");
        
        return "board/" + categoryPath + "/write";
    }
    
    /**
     * 게시글 등록 처리
     */
    @PostMapping("/{categoryPath}/write")
    public String insertPost(@PathVariable String categoryPath,@Valid @ModelAttribute PostCreateDto postCreateDto,
            				BindingResult bindingResult,HttpServletRequest request,Model model,Authentication authentication,RedirectAttributes redirectAttributes) {
        
    	CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
    	
        Map<String, CategoryInfo> categoryMap = codeService.getCategoryPathMap("BOARD_CATEGORY");
        CategoryInfo categoryInfo = categoryMap.get(categoryPath);
        
        if (categoryInfo == null) {
            return "redirect:/board";
        }
        
        log.info("게시글 등록 처리 시작 - categoryPath={}, user={}",categoryPath, authentication.getName());
        
    	// 유효성 검증 실패 시 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            log.warn("게시글 수정 유효성 검증 실패 - , errors: {}", bindingResult.getAllErrors());
            
            model.addAttribute("categoryName", categoryInfo.getName());
            model.addAttribute("categoryPath", categoryPath);
            model.addAttribute("title", "게시글 작성");
            
            return "board/" + categoryPath + "/write";
        }
        
        try {
        	Long postId = postService.createPost(postCreateDto, categoryInfo.getCode(),user,getClientIp(request));
            
            log.info("게시글 작성 완료 - postId: {}, user: {}", postId, authentication.getName());
            
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 등록되었습니다.");
            return "redirect:/board/" + categoryPath + "/view/" + postId;
            
        } catch (IllegalArgumentException e) {
            log.warn("게시글 작성 실패 - user: {}, error: {}", authentication.getName(), e.getMessage());
            bindingResult.reject("createFailed", e.getMessage());
            
            model.addAttribute("categoryName", categoryInfo.getName());
            model.addAttribute("categoryPath", categoryPath);
            model.addAttribute("title", "새 글 작성");
            
            return "board/" + categoryPath + "/write";
            
        } catch (Exception e) {
            log.error("게시글 작성 처리 실패 - user: {}, error: {}", 
                     authentication.getName(), e.getMessage(), e);
            
            redirectAttributes.addFlashAttribute("error", "게시글 작성 중 오류가 발생했습니다.");
            return "redirect:/board/" + categoryPath + "/write";
        }
    }
    
    
    /**
     * 게시글 수정 폼 페이지
     */
    @GetMapping("/{categoryPath}/edit/{postId}")
    public String editPostForm(@PathVariable String categoryPath,@PathVariable Long postId,HttpServletRequest request,Model model,Authentication authentication) {
        
    	CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
    	
        Map<String, CategoryInfo> categoryMap = codeService.getCategoryPathMap("BOARD_CATEGORY");
        CategoryInfo categoryInfo = categoryMap.get(categoryPath);
        
        if (categoryInfo == null) {
            return "redirect:/board";
        }
        
        log.info("게시글 수정 폼 조회 - categoryPath={}, postId={}", categoryPath, postId);
        
        try {
            PostDto post = postService.getPostDetail(postId, getClientIp(request));
            
            // 게시글이 존재하지 않거나 삭제된 경우
            if (post == null || post.isDeleted()) {
                log.warn("존재하지 않는 게시글 - ID: {}", postId);
                return "redirect:/board/" + categoryPath + "/list?error=notFound";
            }
            
            // 카테고리 검증
            if (!categoryInfo.getCode().equals(post.getCategoryCode())) {
                log.warn("카테고리 불일치 - URL: {}, 게시글: {}", categoryPath, post.getCategoryCode());
                return "redirect:/board/" + categoryPath + "/list?error=invalidCategory";
            }
            
            // 권한 검증 (작성자 본인 또는 관리자만 수정 가능)
            Long currentMemberId = user.getMemberId();
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            
            if (!post.getMemberId().equals(currentMemberId) && !isAdmin) {
                log.warn("게시글 수정 권한 없음 - postId: {}, user: {}, author: {}", postId, currentMemberId, post.getAuthorNickname());
                return "redirect:/board/" + categoryPath + "/view/" + postId + "?error=noPermission";
            }
            
            log.info("게시글 수정 폼 조회 성공 - postId={}", postId);
            PostEditDto postEditDto = new PostEditDto(post.getTitle(), post.getContent());
            model.addAttribute("postEditDto", postEditDto);
            
            model.addAttribute("post", post);
            model.addAttribute("categoryName", categoryInfo.getName());
            model.addAttribute("categoryPath", categoryPath);
            model.addAttribute("title", "게시글 수정");
            
            return "board/" + categoryPath + "/edit";
            
        } catch (Exception e) {
            log.error("게시글 수정 폼 조회 실패 - postId: {}, error: {}", postId, e.getMessage(), e);
            return "redirect:/board/" + categoryPath + "/list?error=system";
        }
    }
    
    /**
     * 게시글 수정 처리
     */
    @PostMapping("/{categoryPath}/edit/{postId}")
    public String editPost(@PathVariable String categoryPath,@PathVariable Long postId,@Valid @ModelAttribute PostEditDto postEditDto,
            				BindingResult bindingResult,HttpServletRequest request,Model model,Authentication authentication,RedirectAttributes redirectAttributes) {
        
        Map<String, CategoryInfo> categoryMap = codeService.getCategoryPathMap("BOARD_CATEGORY");
        CategoryInfo categoryInfo = categoryMap.get(categoryPath);
        
        if (categoryInfo == null) {
            return "redirect:/board";
        }
        
        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();
        
        // 유효성 검증 실패 시 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            log.warn("게시글 수정 유효성 검증 실패 - postId: {}, errors: {}", postId, bindingResult.getAllErrors());
            
            model.addAttribute("categoryName", categoryInfo.getName());
            model.addAttribute("categoryPath", categoryPath);
            model.addAttribute("title", "게시글 수정");
            return "board/" + categoryPath + "/edit";
        }
        
        try {
            postService.updatePost(postId, postEditDto, user, getClientIp(request));
            
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/board/" + categoryPath + "/view/" + postId;
            
        } catch (IllegalArgumentException e) {
            if ("변경된 내용이 없습니다.".equals(e.getMessage())) {
                redirectAttributes.addFlashAttribute("message", e.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
            return "redirect:/board/" + categoryPath + (e.getMessage().contains("권한") ? "/view/" : "/edit/") + postId;
            
        } catch (Exception e) {
            log.error("게시글 수정 실패 - postId: {}, error: {}", postId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "게시글 수정 중 오류가 발생했습니다.");
            return "redirect:/board/" + categoryPath + "/edit/" + postId;
        }
    }
    
    @PostMapping("/api/posts/{postId}/like") // API 경로 지정
    @ResponseBody // JSON 응답을 위해 추가
    public ResponseEntity<LikeResponseDto> toggleLike(@PathVariable("postId") Long postId,@AuthenticationPrincipal CustomUserDetails user) { // @AuthenticationPrincipal 사용 추천

        if (user == null) {
            // 비로그인 사용자는 접근 불가 (401 Unauthorized)
            return ResponseEntity.status(401).build();
        }

        Long memberId = user.getMemberId();
        LikeResponseDto response = postLikeService.toggleLike(postId, memberId);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 클라이언트 IP 주소 추출
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

}