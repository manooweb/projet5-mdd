import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { PostService } from '../post.service';
import { PostCommentsComponent } from './post-comments.component';

describe('PostCommentsComponent', () => {
  const postService = {
    createComment: vi.fn(),
  };

  beforeEach(() => {
    postService.createComment.mockReset();
    postService.createComment.mockReturnValue(of(void 0));
  });

  it('renders the comments list before the comment form', async () => {
    await TestBed.configureTestingModule({
      imports: [PostCommentsComponent],
      providers: [{ provide: PostService, useValue: postService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostCommentsComponent);
    fixture.componentRef.setInput('comments', [
      { author: 'demo', content: 'Un commentaire utile.', createdAt: '2026-08-23T10:20:30Z' },
    ]);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const commentsList = hostElement.querySelector('[aria-label="Liste des commentaires"]');
    const form = hostElement.querySelector('form');
    const textarea = hostElement.querySelector<HTMLTextAreaElement>('#content');
    const sendButton = hostElement.querySelector<HTMLButtonElement>(
      'button[aria-label="Envoyer le commentaire"]',
    );

    expect(hostElement.querySelector('h2')?.textContent?.trim()).toBe('Commentaires');
    expect(commentsList).not.toBeNull();
    expect(form).not.toBeNull();
    expect(commentsList!.compareDocumentPosition(form!) & Node.DOCUMENT_POSITION_FOLLOWING).toBe(
      Node.DOCUMENT_POSITION_FOLLOWING,
    );
    expect(textarea).not.toBeNull();
    expect(commentsList?.textContent).toContain('demo');
    expect(commentsList?.textContent).toContain('Un commentaire utile.');
    expect(sendButton?.querySelector('svg')).not.toBeNull();
    expect(sendButton?.disabled).toBe(true);
  });

  it('sends the typed comment for the displayed post', async () => {
    await TestBed.configureTestingModule({
      imports: [PostCommentsComponent],
      providers: [{ provide: PostService, useValue: postService }],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostCommentsComponent);
    fixture.componentRef.setInput('postId', 12);
    fixture.detectChanges();

    const hostElement = fixture.nativeElement as HTMLElement;
    const textarea = hostElement.querySelector<HTMLTextAreaElement>('#content');
    if (!textarea) {
      throw new Error('Comment textarea was not found.');
    }
    textarea.value = 'Un commentaire utile.';
    textarea.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    hostElement.querySelector('form')?.dispatchEvent(new Event('submit'));

    expect(postService.createComment).toHaveBeenCalledWith(12, {
      content: 'Un commentaire utile.',
    });
  });
});
