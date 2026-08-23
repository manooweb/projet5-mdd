import { TestBed } from '@angular/core/testing';
import { PostCommentsComponent } from './post-comments.component';

describe('PostCommentsComponent', () => {
  it('renders the comments list before the comment form', async () => {
    await TestBed.configureTestingModule({
      imports: [PostCommentsComponent],
    }).compileComponents();

    const fixture = TestBed.createComponent(PostCommentsComponent);
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
    expect(sendButton?.querySelector('svg')).not.toBeNull();
    expect(sendButton?.disabled).toBe(true);
  });
});
