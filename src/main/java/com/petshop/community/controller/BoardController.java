package com.petshop.community.controller;


import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.petshop.community.dto.PostDto;
import com.petshop.community.dto.PostEditDto;
import com.petshop.community.dto.PostSearchRequest;
import com.petshop.community.service.CodeService;
import com.petshop.community.service.CodeService.CategoryInfo;
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
    public String getPostDetail(@PathVariable String categoryPath,@PathVariable Long postId,HttpServletRequest request,Model model) {
        
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
     * 게시글 수정 폼 페이지
     */
    @GetMapping("/{categoryPath}/edit/{postId}")
    public String editPostForm(@PathVariable String categoryPath,@PathVariable Long postId,HttpServletRequest request,Model model,Authentication authentication) {
        
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
            String currentUser = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            
            if (!post.getAuthorNickname().equals(currentUser) && !isAdmin) {
                log.warn("게시글 수정 권한 없음 - postId: {}, user: {}, author: {}", postId, currentUser, post.getAuthorNickname());
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
        
        log.info("게시글 수정 처리 시작 - categoryPath={}, postId={}, user={}",categoryPath, postId, authentication.getName());
        
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
            String currentUser = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            
            if (!post.getAuthorNickname().equals(currentUser) && !isAdmin) {
                log.warn("게시글 수정 권한 없음 - postId: {}, user: {}, author: {}", postId, currentUser, post.getAuthorNickname());
                return "redirect:/board/" + categoryPath + "/view/" + postId + "?error=noPermission";
            }
            
        	// 유효성 검증 실패 시 폼으로 다시 이동
            if (bindingResult.hasErrors()) {
                log.warn("게시글 수정 유효성 검증 실패 - postId: {}, errors: {}", postId, bindingResult.getAllErrors());
                
                model.addAttribute("post", post);
                model.addAttribute("categoryName", categoryInfo.getName());
                model.addAttribute("categoryPath", categoryPath);
                model.addAttribute("title", "게시글 수정");
                
                return "board/" + categoryPath + "/edit";
            }
            
            // 내용 변경 여부 확인
            if (post.getTitle().equals(postEditDto.getTitle()) && post.getContent().equals(postEditDto.getContent())) {
            	
                log.info("게시글 내용 변경 없음 - postId: {}", postId);
                redirectAttributes.addFlashAttribute("message", "변경된 내용이 없습니다.");
                return "redirect:/board/" + categoryPath + "/view/" + postId;
            }
            
            // 게시글 업데이트
            PostDto updatePost = new PostDto();
            updatePost.setId(postId);
            updatePost.setTitle(postEditDto.getTitle().trim());
            updatePost.setContent(postEditDto.getContent().trim());
            
            postService.updatePost(updatePost, authentication.getName());
            
            log.info("게시글 수정 완료 - postId: {}, user: {}", postId, authentication.getName());
            
            redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
            return "redirect:/board/" + categoryPath + "/view/" + postId;
            
        } catch (IllegalArgumentException e) {
            log.warn("게시글 수정 실패 - postId: {}, error: {}", postId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/board/" + categoryPath + "/edit/" + postId;
            
        } catch (Exception e) {
            log.error("게시글 수정 처리 실패 - postId: {}, error: {}", postId, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "게시글 수정 중 오류가 발생했습니다.");
            return "redirect:/board/" + categoryPath + "/edit/" + postId;
        }
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