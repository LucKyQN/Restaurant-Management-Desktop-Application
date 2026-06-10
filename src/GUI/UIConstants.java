package GUI;

import java.awt.Color;
import java.awt.Font;

/**
 * Central Design System — all color and font tokens for the modern UI.
 * Import this class in any screen to access shared visual constants.
 */
public final class UIConstants {

    private UIConstants() {} // utility class, no instances

    // ── Primary Brand ─────────────────────────────────────────────────────────
    /** Main brand red */
    public static final Color PRIMARY        = new Color(0xDC2626);
    /** Darker red for gradients / hover */
    public static final Color PRIMARY_DARK   = new Color(0xB91C1C);
    /** Very light red tint for selected backgrounds */
    public static final Color PRIMARY_TINT   = new Color(0xFEF2F2);

    // ── Backgrounds ──────────────────────────────────────────────────────────
    /** App-level page background */
    public static final Color BG_APP         = new Color(0xF3F4F6);
    /** Card / panel background */
    public static final Color CARD_BG        = Color.WHITE;
    /** Topbar / sidebar background */
    public static final Color BG_SURFACE     = Color.WHITE;

    // ── Text ─────────────────────────────────────────────────────────────────
    /** Primary text — near-black */
    public static final Color TEXT_DARK      = new Color(0x1F2937);
    /** Secondary / muted text */
    public static final Color TEXT_GRAY      = new Color(0x6B7280);
    /** Very light placeholder text */
    public static final Color TEXT_PLACEHOLDER = new Color(0x9CA3AF);

    // ── Borders ──────────────────────────────────────────────────────────────
    /** Standard border color */
    public static final Color BORDER         = new Color(0xE5E7EB);
    /** Input field border */
    public static final Color BORDER_INPUT   = new Color(0xD1D5DB);
    /** Input focused border */
    public static final Color BORDER_FOCUS   = new Color(0xDC2626);

    // ── Status — Table States ─────────────────────────────────────────────────
    public static final Color BG_TRONG       = new Color(0xDCFCE7);
    public static final Color BORDER_TRONG   = new Color(0x22C55E);
    public static final Color BG_KHACH       = new Color(0xFEE2E2);
    public static final Color BORDER_KHACH   = new Color(0xEF4444);
    public static final Color BG_DAT         = new Color(0xFEF9C3);
    public static final Color BORDER_DAT     = new Color(0xEAB308);
    public static final Color BG_GHEP        = new Color(0xFFEDD5);
    public static final Color BORDER_GHEP    = new Color(0xF97316);
    public static final Color BG_SAFE        = new Color(0xE0F2FE);
    public static final Color BORDER_SAFE    = new Color(0x3B82F6);

    // ── Status — Dish/Service States ─────────────────────────────────────────
    /** "Đã lên" (served) badge colors */
    public static final Color BADGE_GREEN_BG = new Color(0xDCFCE7);
    public static final Color BADGE_GREEN_FG = new Color(0x15803D);
    /** "Chưa lên" (pending) badge colors */
    public static final Color BADGE_RED_BG   = new Color(0xFEE2E2);
    public static final Color BADGE_RED_FG   = new Color(0xB91C1C);
    /** "Mang về" (takeout) badge colors */
    public static final Color BADGE_BLUE_BG  = new Color(0xDBEAFE);
    public static final Color BADGE_BLUE_FG  = new Color(0x1D4ED8);
    /** "Hủy" (cancelled) badge colors */
    public static final Color BADGE_GRAY_BG  = new Color(0xF3F4F6);
    public static final Color BADGE_GRAY_FG  = new Color(0x6B7280);

    // ── Shadow ───────────────────────────────────────────────────────────────
    public static final Color SHADOW_OUTER   = new Color(0, 0, 0, 8);
    public static final Color SHADOW_INNER   = new Color(0, 0, 0, 4);

    // ── Fonts (Segoe UI) ──────────────────────────────────────────────────────
    public static final Font FONT_BOLD_20    = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_BOLD_18    = new Font("Segoe UI", Font.BOLD,  18);
    public static final Font FONT_BOLD_16    = new Font("Segoe UI", Font.BOLD,  16);
    public static final Font FONT_BOLD_15    = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BOLD_14    = new Font("Segoe UI", Font.BOLD,  14);
    public static final Font FONT_BOLD_13    = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_PLAIN_15   = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_PLAIN_14   = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_PLAIN_13   = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_PLAIN_12   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_ITALIC_13  = new Font("Segoe UI", Font.ITALIC, 13);
    public static final Font FONT_BOLD_12    = new Font("Segoe UI", Font.BOLD,  12);
    public static final Font FONT_EMOJI_28   = new Font("Segoe UI Emoji", Font.PLAIN, 28);
    public static final Font FONT_EMOJI_22   = new Font("Segoe UI Emoji", Font.PLAIN, 22);
}
